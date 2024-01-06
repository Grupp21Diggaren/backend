package Entity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class srAPI {
    public static void main(String[] args) {
        try {
            // Byt ut CHANNEL_ID med den önskade kanalens ID
            String channelID = "2576";

            // Bygg URL för att hämta låtlistan för "just nu"
            String apiUrl = "http://api.sr.se/api/v2/playlists/rightnow?channelid=" + channelID;

            // Gör HTTP GET-anrop till API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Läs svaret
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line + "\n");
            }
            reader.close();

            // Bearbeta JSON-svaret
            // Här måste du implementera kod för att extrahera och använda den önskade informationen från JSON-svaret

            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
