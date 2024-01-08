package Entity;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.hc.core5.http.ParseException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.ForbiddenException;
import se.michaelthelin.spotify.exceptions.detailed.NotFoundException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

public class SpotifyAPI {
    private static final String clientId = "4f1acd03d402428fb603670ca0c8ec72";
    private static final String clientSecret = "f48846be71df46edbfbb62e72d24ca0d";
    private static final String redirectUri = "http://localhost:5000"; // Kan behövas ändra.
    private static final String scopes = "user-read-private user-read-email user-library-read user-library-modify playlist-modify-public playlist-modify-private";

    private static List<PlaylistSimplified> playlist = new ArrayList<>();
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(URI.create(redirectUri))
            .build();

    public static void main(String[] args) throws Exception {
        // Obtain access token using authorization code flow with PKCE
        authorize();

        // Ask what song
        askUserWhichSongToAdd();
    }

    private static void authorize() {
        try {
            // Set up the authorization URI request with the required scopes
            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                    .scope(scopes)
                    .show_dialog(true)
                    .build();

            // Get the authorization URI
            String authorizationUri = String.valueOf(authorizationCodeUriRequest.execute());

            // Direct the user to the authorization URI to grant permissions
            openWebPage(URI.create(authorizationUri));

            // Retrieve the authorization code from the user's manual input or redirect URI
            System.out.print("Enter the authorization code from the redirected URI: ");
            Scanner scanner = new Scanner(System.in);
            String authorizationCode = scanner.nextLine().trim();

            // Set up the authorization code request
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authorizationCode)
                    .build();

            // Retrieve the access and refresh tokens
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Access token obtained successfully: " + authorizationCodeCredentials.getAccessToken());
            System.out.println("Refresh token: " + authorizationCodeCredentials.getRefreshToken());
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error during authorization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void askUserWhichSongToAdd() throws Exception {
        //Scanner scanner = new Scanner(System.in);
        //System.out.print("Enter the name of a song you wish to add: ");
        String trackName = getRadioSongs();
        List<Track> tracks = searchForTrack(trackName);

        if (!tracks.isEmpty()) {
            // Let the user choose a track
            int selectedTrackIndex = selectTrack(tracks);
            if (selectedTrackIndex != -1) {
                Track selectedTrack = tracks.get(selectedTrackIndex);

                // Get the user's playlists
                List<PlaylistSimplified> playlists = getUserPlaylists();

                // Let the user choose a playlist
                String selectedPlaylistId = showAndChooseUserPlaylists(playlists);
                if (selectedPlaylistId != null) {
                    // Add the selected track to the chosen playlist
                    addTrackToPlaylist(selectedPlaylistId, selectedTrack.getId());
                }
            }
        }
    }

    private static String getRadioSongs() throws Exception {
        RadioAPI sverigesRadio = new RadioAPI();
        sverigesRadio.getCurrentPlaylist(2576);
        String nameAndArtist = sverigesRadio.getCurrTitle() + " " + sverigesRadio.getCurrArtist();
        return nameAndArtist;
    }

    public static List<PlaylistSimplified> getUserPlaylists() {
        try {
            // Get the current user's playlists
            final GetListOfCurrentUsersPlaylistsRequest playlistsRequest = spotifyApi.getListOfCurrentUsersPlaylists()
                    .limit(10)
                    .build();
            final Paging<PlaylistSimplified> playlistsPaging = playlistsRequest.execute();
            playlist = Arrays.asList(playlistsPaging.getItems());
            return playlist;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.err.println("Error getting user's playlists: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    private static List<Track> searchForTrack(String trackName) {
        try {
            // Search for tracks by name
            final SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(trackName)
                    .limit(5)
                    .build();
            final Paging<Track> trackPaging = searchTracksRequest.execute();

            return Arrays.asList(trackPaging.getItems());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.err.println("Error searching for track: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    private static int selectTrack(List<Track> tracks) {
        System.out.println();
        System.out.println("Search Results:");
        for (int i = 0; i < tracks.size(); i++) {
            System.out.println((i + 1) + ". " + tracks.get(i).getName() + " - " + getArtistsString(tracks.get(i).getArtists()));
        }

        System.out.print("Enter the number of the track to add to a playlist (or 0 to cancel): ");
        Scanner scanner = new Scanner(System.in);
        int userInput = scanner.nextInt();

        if (userInput >= 1 && userInput <= tracks.size()) {
            return userInput - 1;
        } else if (userInput == 0) {
            System.out.println("Operation canceled.");
            return -1;
        } else {
            System.out.println("Invalid input. Operation canceled.");
            return -1;
        }
    }

    private static String showAndChooseUserPlaylists(List<PlaylistSimplified> playlists) {
        try {
            if (playlists.isEmpty()) {
                System.out.println("No playlists available.");
                return null;
            }
            System.out.println();
            System.out.println("Available Playlists:");
            for (int i = 0; i < playlists.size(); i++) {
                System.out.println((i + 1) + ". " + playlists.get(i).getName());
            }

            System.out.print("Enter the number of the playlist to add the song to (or 0 to cancel): ");
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine().trim();  // Parse as a string
            try {
                int playlistNumber = Integer.parseInt(userInput);
                if (playlistNumber >= 1 && playlistNumber <= playlists.size()) {
                    return playlists.get(playlistNumber - 1).getId();
                } else if (playlistNumber == 0) {
                    System.out.println("Operation canceled.");
                    return null;
                } else {
                    System.out.println("Invalid input. Operation canceled.");
                    return null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Operation canceled.");
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting user's playlists: " + e.getMessage(), e);
        }
    }

    private static void addTrackToPlaylist(String playlistId, String trackId) throws IOException, ParseException, SpotifyWebApiException {
        // Print the track and playlist IDs to check if they are correct
        System.out.println("Adding track " + trackId + " to playlist " + playlistId);

        // Step 3: Add the track to the playlist
        AddItemsToPlaylistRequest addTracksRequest = spotifyApi
                .addItemsToPlaylist(playlistId, new String[]{"spotify:track:" + trackId})
                .build();

        try {
            addTracksRequest.execute();
            System.out.println("Track added successfully!");
        } catch (NotFoundException e) {
            System.out.println("Track or playlist not found. Check if the IDs are correct.");
            e.printStackTrace();
        } catch (ForbiddenException e) {
            System.out.println("Insufficient permissions. Make sure the app has the necessary scopes.");
            e.printStackTrace();
        } catch (SpotifyWebApiException e) {
            System.out.println("Error adding track to playlist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getArtistsString(ArtistSimplified[] artists) {
        StringBuilder artistsString = new StringBuilder();
        for (int i = 0; i < artists.length; i++) {
            artistsString.append(artists[i].getName());
            if (i < artists.length - 1) {
                artistsString.append(", ");
            }
        }
        return artistsString.toString();
    }

    private static void openWebPage(URI uri) {
        try {
            java.awt.Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            System.out.println("Please open the following URL in your browser to continue the authentication:");
            System.out.println(uri);
        }
    }

    private static final String SPOTIFY_API_URL = "https://api.spotify.com/v1/me/playlists";
    private static final String PLAYLIST_NAME = "Diggaren";

    public void createPlaylist(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(
                "{\"name\":\"" + PLAYLIST_NAME + "\",\"public\":false,\"collaborative\":false,\"description\":\"Your playlist description here\"}",
                headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(SPOTIFY_API_URL, request, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Playlist created: " + responseEntity.getBody());
        } else {
            System.err.println("Failed to create playlist. Status code: " + responseEntity.getStatusCode());
        }
    }

    public static List<PlaylistSimplified> getPlaylist() {
        return playlist;
    }
}
