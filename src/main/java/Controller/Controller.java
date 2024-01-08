package Controller;

import Entity.RadioAPI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Entity.WikiAPI;

@RestController
//@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controller {
    RadioAPI radioAPI = new RadioAPI();

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

}
