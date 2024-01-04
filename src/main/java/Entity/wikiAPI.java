package Entity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class wikiAPI {
    public wikiAPI() {
        getBiographyFromWikiAPI();
    }

    public String getBiographyFromWikiAPI() {
        String apiUrl = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&titles=Michael_Jackson&redirects=true";

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrl);

        String[] ct = new String[0];
        try {
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
            ct = cleanedText.split("<p>");
            //System.out.println("First Paragraph: " + ct[2]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ct[2];
    }
}


