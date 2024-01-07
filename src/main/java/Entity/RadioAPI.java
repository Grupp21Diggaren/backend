package Entity;

import org.apache.http.client.fluent.Request;
import org.json.JSONObject;

public class RadioAPI {
    private static final String URLSong = "https://api.sr.se/api/v2/playlists/rightnow?format=json&channelid=";

    String title = "No information available";
    String artist = "No information available";

    public String getCurrentPlaylist(int Id) throws Exception {
        String urlSong = URLSong + Id;
        String jsonResponseSong = Request.Get(urlSong).execute().returnContent().asString();

      //  System.out.println("JSON Response: " + jsonResponseSong);


        // Parse JSON response
        JSONObject jsonObjectSong = new JSONObject(jsonResponseSong);
        JSONObject songPlaylist = jsonObjectSong.getJSONObject("playlist");

        // default value
        String previously = "";
        String nowPlaying = "";
        String upNext = "";


        if (songPlaylist.has("previoussong")) {
           // System.out.println("in prevsong");
            JSONObject previousSong = songPlaylist.getJSONObject("previoussong");
            previously = formatSongDetails(previousSong);

        }


        if (songPlaylist.has("song")) {
           // System.out.println("in currsong");
            JSONObject currentSong = songPlaylist.getJSONObject("song");
            nowPlaying = formatSongDetails(currentSong);
        }


        if (songPlaylist.has("nextsong")) {
          //  System.out.println("in nextsong");
            JSONObject nextSong = songPlaylist.getJSONObject("nextsong");
            upNext = formatSongDetails(nextSong);
        }


        return "Previously: " + previously + "\nNow Playing: " + nowPlaying + "\nNext Song: " + upNext;
    }


    private  String formatSongDetails(JSONObject songObject) {
        String title = songObject.optString("title");

        setTitle(title);
        String artist = songObject.optString("artist");
        setArtist(artist);
        return title + " by " + artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public static void main(String[] args) {
        RadioAPI radioAPI = new RadioAPI();

        try {
            int channelId = 2576; // Example Channel ID
            // p3=164, din gata = 2576
            String playlistDetails = radioAPI.getCurrentPlaylist(channelId);
            System.out.println(playlistDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}