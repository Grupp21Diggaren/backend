package Entity;

import org.apache.http.client.fluent.Request;
import org.json.JSONObject;

public class RadioAPI {
    private static final String URLSong = "https://api.sr.se/api/v2/playlists/rightnow?format=json&channelid=";
    //private static final String URLFindEpisodeId = "https://api.sr.se/api/v2/episodes/search/?format=json&kanalid=";
    //private static final String URLEpisode = "https://api.sr.se/api/v2/playlists/getplaylistbyepisodeid?format=json&id=";



    public static String getCurrentPlaylist(int Id) throws Exception {
        String urlSong = URLSong + Id;
        String jsonResponseSong = Request.Get(urlSong).execute().returnContent().asString();

        System.out.println("JSON Response: " + jsonResponseSong);


      //  String urlFindEpisodeId = URLFindEpisodeId + Id;
        // String jsonResponseEpisodeId = Request.Get(urlFindEpisodeId).execute().returnContent().asString();



        // Parse JSON response
        JSONObject jsonObjectSong = new JSONObject(jsonResponseSong);
        JSONObject songPlaylist = jsonObjectSong.getJSONObject("playlist");

        // Extract song details
        String previously = "";
        String nowPlaying = "";
        String upNext = "";




        if(songPlaylist.has("previoussong")){
            System.out.println("in prevsong");
            JSONObject previousSong = songPlaylist.getJSONObject("previoussong");
            previously = formatSongDetails(previousSong);

        }
      // else {
      //     JSONObject jsonObjectEpisodeId = new JSONObject(jsonResponseEpisodeId);
      //     JSONObject episodeId = jsonObjectEpisodeId.getJSONObject("episode");
      //
      //     if (episodeId.has("id")){
      //         String id = episodeId.optString("ID");
      //
      //         String urlFindEpisode = URLEpisode + id;
      //         String jsonResponseEpisode = Request.Get(urlFindEpisode).execute().returnContent().asString();
      //         JSONObject jsonObjectEpisode = new JSONObject(jsonResponseEpisode);
      //
      //         previously = formatSongDetails(jsonObjectEpisode);
      //     }
      // }

        if (songPlaylist.has("song")) {
            System.out.println("in currsong");
            JSONObject currentSong = songPlaylist.getJSONObject("song");
            nowPlaying = formatSongDetails(currentSong);
        }

      // else {
      //     JSONObject jsonObjectEpisodeId = new JSONObject(jsonResponseEpisodeId);
      //     JSONObject episodeId = jsonObjectEpisodeId.getJSONObject("episode");
      //
      //     if (episodeId.has("id")){
      //         String id = episodeId.optString("ID");
      //
      //         String urlFindEpisode = URLEpisode + id;
      //         String jsonResponseEpisode = Request.Get(urlFindEpisode).execute().returnContent().asString();
      //         JSONObject jsonObjectEpisode = new JSONObject(jsonResponseEpisode);
      //
      //         nowPlaying = formatSongDetails(jsonObjectEpisode);
      //     }
      // }

        if (songPlaylist.has("nextsong")) {
            System.out.println("in nextsong");
            JSONObject nextSong = songPlaylist.getJSONObject("nextsong");
            upNext = formatSongDetails(nextSong);
        }

     //   else {
     //       JSONObject jsonObjectEpisodeId = new JSONObject(jsonResponseEpisodeId);
     //       JSONObject episodeId = jsonObjectEpisodeId.getJSONObject("episode");
     //
     //       if (episodeId.has("id")){
     //           String id = episodeId.optString("ID");
     //
     //           String urlFindEpisode = URLEpisode + id;
     //           String jsonResponseEpisode = Request.Get(urlFindEpisode).execute().returnContent().asString();
     //           JSONObject jsonObjectEpisode = new JSONObject(jsonResponseEpisode);
     //
     //           upNext = formatSongDetails(jsonObjectEpisode);
     //       }
     //   }






        return "Previously: " + previously  + "\nNow Playing: " + nowPlaying + "\nNext Song: " + upNext;
    }

    // public static int findEpisode(String id) throws Exception{
    //
    //     String urlFindEpisode = URLEpisode + id;
    //     String jsonResponseEpisode = Request.Get(urlFindEpisode).execute().returnContent().asString();
    //     JSONObject jsonObjectEpisode = new JSONObject(jsonResponseEpisode);
    //
    //     formatSongDetails(jsonObjectEpisode);
    //
    //
    // }

    private static String formatSongDetails(JSONObject songObject) {
        String title = songObject.optString("title");
        String artist = songObject.optString("artist");
        return title + " by " + artist;
    }


    public static void main(String[] args) {
        try {
            int channelId = 2576; // Example Channel ID
            // p3=164, din gata = 2576
            String playlistDetails = RadioAPI.getCurrentPlaylist(channelId);
            System.out.println(playlistDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}