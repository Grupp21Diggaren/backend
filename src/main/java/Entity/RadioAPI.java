package Entity;

import org.apache.http.client.fluent.Request;
import org.json.JSONObject;

public class RadioAPI {
    private static final String URLSong = "https://api.sr.se/api/v2/playlists/rightnow?format=json&channelid=";


    private String currTitle = "No information available";
    private String currArtist = "No information available";
    private String prevTitle = "No information available";
    private String prevArtist = "No information available";
    private String nextTitle = "No information available";
    private String nextArtist = "No information available";

    public void getCurrentPlaylist(int Id) throws Exception {
        String urlSong = URLSong + Id;
        String jsonResponseSong = Request.Get(urlSong).execute().returnContent().asString();

          System.out.println("JSON Response: " + jsonResponseSong);


        // Parse JSON response
        JSONObject jsonObjectSong = new JSONObject(jsonResponseSong);
        JSONObject songPlaylist = jsonObjectSong.getJSONObject("playlist");

        // default value
        // String previously = "No information available";
        // String nowPlaying = "No information available";
        // String upNext = "No information available";


        if (songPlaylist.has("previoussong")) {
            // System.out.println("in prevsong");
            JSONObject previousSong = songPlaylist.getJSONObject("previoussong");
            prevTitle = previousSong.optString("title");

            prevArtist = previousSong.optString("artist");


        }


        if (songPlaylist.has("song")) {
            // System.out.println("in currsong");
            JSONObject currentSong = songPlaylist.getJSONObject("song");

            currTitle = currentSong.optString("title");

            currArtist = currentSong.optString("artist");
        }


        if (songPlaylist.has("nextsong")) {
            //  System.out.println("in nextsong");
            JSONObject nextSong = songPlaylist.getJSONObject("nextsong");
            nextTitle = nextSong.optString("title");

            nextArtist = nextSong.optString("artist");
        }


    }


    //  private String formatSongDetails(JSONObject songObject) {
    //
    //      String title = songObject.optString("title");
    //
    //      String artist = songObject.optString("artist");
    //
    //      return title + " by " + artist;
    //  }

    public String getCurrTitle() {
        return currTitle;
    }

    public String getCurrArtist() {
        return currArtist;
    }


    public String getPrevTitle() {
        return prevTitle;
    }


    public String getPrevArtist() {
        return prevArtist;
    }


    public String getNextTitle() {
        return nextTitle;
    }

    public String getNextArtist() {
        return nextArtist;
    }

    public static void main(String[] args) {
        RadioAPI radioAPI = new RadioAPI();

        try {
            int channelId = 2576; // Example Channel ID
            // p3=164, din gata = 2576
           // String playlistDetails = radioAPI.getCurrentPlaylist(channelId);

            radioAPI.getCurrentPlaylist(channelId);
             System.out.println("prev song: " + radioAPI.getPrevTitle() + " by " + radioAPI.getPrevArtist() +
                                "\ncurr song: " + radioAPI.getCurrTitle() + " by " + radioAPI.getCurrArtist() +
                                "\nnext song: " + radioAPI.getNextTitle() + " by " + radioAPI.getNextArtist());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}