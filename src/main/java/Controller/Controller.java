package Controller;

import Entity.RadioAPI;
import Entity.SpotifyAPI;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import Entity.WikiAPI;
import org.springframework.web.client.RestTemplate;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@RestController
//@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controller {
    RadioAPI radioAPI = new RadioAPI();
    SpotifyAPI spotifyAPI = new SpotifyAPI();

    public Controller() {

    }

    @GetMapping("/api/test")
    public String getString() {
        return "Here is the string!";
    }

    @GetMapping("/api/biography/{artistName}")
    public ResponseEntity<String> getBiography(@PathVariable("artistName") String artistName) {
        System.out.println(WikiAPI.getBiographyFromWikiAPI(artistName));
        String bio = WikiAPI.getBiographyFromWikiAPI(artistName);
        if (bio != null) {
            return ResponseEntity.ok(bio);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Biography not found for " + artistName);
        }
        //return "Biography for " + artistName + ": " + WikiAPI.getBiographyFromWikiAPI(artistName);
    }

    @GetMapping("/api/getCurrentArtist")
    public ResponseEntity<String> getCurrentArtist() throws Exception {
        radioAPI.getCurrentPlaylist(2576); //ÄNDRA

        String currArtist = radioAPI.getCurrArtist();
        if (currArtist != null) {
            return ResponseEntity.ok(currArtist);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Current artist not found");
        }
    }

    @GetMapping("/api/getCurrentTitle")
    public ResponseEntity<String> getCurrentTitle() throws Exception {
        radioAPI.getCurrentPlaylist(2576); //ÄNDRA

        String currTitle = radioAPI.getCurrTitle();
        if (currTitle != null) {
            return ResponseEntity.ok(currTitle);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Current artist not found");
        }
    }

    @GetMapping("/api/getPreviousArtist")
    public ResponseEntity<String> getPreviousArtist() throws Exception {
        radioAPI.getCurrentPlaylist(2576); //ÄNDRA

        String prevArtist = radioAPI.getPrevArtist();
        if (prevArtist != null) {
            return ResponseEntity.ok(prevArtist);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Current artist not found");
        }
    }

    @GetMapping("/api/getPreviousTitle")
    public ResponseEntity<String> getPreviousTitle() throws Exception {
        radioAPI.getCurrentPlaylist(2576); //ÄNDRA

        String prevTitle = radioAPI.getPrevTitle();
        if (prevTitle != null) {
            return ResponseEntity.ok(prevTitle);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Current artist not found");
        }
    }

    @GetMapping("/api/login")
    public String login() {
        String client_id = "9f4ca8c501324402bde0c3a7403c59d3";
        String redirect_uri = "http://localhost:80";
        String state = generateRandomString(16);
        String scope = "user-read-private user-read-email user-library-read user-library-modify playlist-modify-public playlist-modify-private";

        return "redirect:" + "https://accounts.spotify.com/authorize?" +
                "response_type=code&" +
                "client_id=" + client_id + "&" +
                "scope=" + scope + "&" +
                "redirect_uri=" + redirect_uri + "&" +
                "state=" + state;
    }
    private String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @GetMapping("/api/createPlaylist/{accessToken}")
    public void createPlaylist(@PathVariable("accessToken") String accessToken){
        spotifyAPI.createPlaylist(accessToken);
    }

    @PostMapping("/api/createPlaylist")
    public ResponseEntity<String> createPlaylist2(String accessToken) {
        String playlistName = "Diggaren";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        String requestBody = String.format("{\"name\":\"%s\",\"public\":true,\"collaborative\":false,\"description\":\"Your playlist description here\"}", playlistName);

        RequestEntity<String> request = RequestEntity
                .post(URI.create("https://api.spotify.com/v1/me/playlists"))
                .headers(headers)
                .body(requestBody);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(request, String.class);
    }

    @GetMapping("/api/spotify/playlists")
    public ResponseEntity<List<PlaylistSimplified>> getPlaylists() {
        // Assume SpotifyAPI has a method called fetchPlaylists that returns a list of Playlist objects
        List<PlaylistSimplified> playlists = spotifyAPI.getUserPlaylists();
        return ResponseEntity.ok(playlists);
    }


}
