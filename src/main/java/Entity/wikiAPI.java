package Entity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

public class wikiAPI {
    static RadioAPI radioAPI = new RadioAPI();
    static String answer = "";
    public wikiAPI() {

    }

    public static String getBiographyFromWikiAPI(String name) {
        try {
            // URL encode the entity name
            String encodedEntityName = URLEncoder.encode(name, "UTF-8");

            // Search for the entity to get potential matches
            String searchUrl = "https://en.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=" + encodedEntityName;
            HttpClient searchHttpClient = HttpClients.createDefault();
            HttpGet searchHttpGet = new HttpGet(searchUrl);

            HttpResponse searchResponse = searchHttpClient.execute(searchHttpGet);
            BufferedReader searchReader = new BufferedReader(new InputStreamReader(searchResponse.getEntity().getContent()));
            StringBuilder searchResult = new StringBuilder();
            String searchLine;
            while ((searchLine = searchReader.readLine()) != null) {
                searchResult.append(searchLine);
            }

            // Parse JSON search response
            JSONObject jsonSearchResponse = new JSONObject(searchResult.toString());
            JSONArray searchResults = jsonSearchResponse.getJSONObject("query").getJSONArray("search");

            if (searchResults.length() > 0) {
                // Choose the first result as the most relevant match
                JSONObject firstResult = searchResults.getJSONObject(0);
                String pageTitle = firstResult.getString("title");

                // Use the chosen page title to fetch the biography
                String apiUrl = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&titles=" + URLEncoder.encode(pageTitle, "UTF-8") + "&redirects=true";
                HttpClient httpClient = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(apiUrl);

                HttpResponse response = httpClient.execute(httpGet);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(result.toString());
                JSONObject pages = jsonResponse.getJSONObject("query").getJSONObject("pages");
                String firstPageId = pages.keys().next();
                JSONObject firstPage = pages.getJSONObject(firstPageId);
                String extract = firstPage.getString("extract");

                // Clean HTML using Jsoup
                String cleanedText = Jsoup.clean(extract, Whitelist.relaxed());
                String[] ct = cleanedText.split("<p>");

                if (ct[1] != null) {
                    answer = ct[1];
                } else if (ct[1] != null && ct[2] != null) {
                    answer = ct[1] + ct[2];
                } else {
                    answer = "The person does not have a Wikipedia page";
                }

                return Jsoup.parse(answer).text();

            } else {
                return "No relevant Wikipedia page found for the entity: " + name;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    public static void main(String[] args) throws Exception {
        radioAPI.getCurrentPlaylist(2576);

        if (!radioAPI.getPrevArtist().equals("No information available")) {
            System.out.println("Prev artist: " + radioAPI.getPrevArtist());
            System.out.println(getBiographyFromWikiAPI(radioAPI.getPrevArtist()));
        }
        if (!radioAPI.getCurrArtist().equals("No information available")){
            System.out.println("Curr artist: " + radioAPI.getCurrArtist());
            System.out.println(getBiographyFromWikiAPI(radioAPI.getCurrArtist()));
        }
        if (radioAPI.getCurrArtist().equals("No information available")) {
            System.out.println("No information available");
        }
        if (radioAPI.getPrevArtist().equals("No information available")) {
            System.out.println("No information available");
        }

        //System.out.println(getBiographyFromWikiAPI(radioAPI.getPrevArtist()));
    }
}


