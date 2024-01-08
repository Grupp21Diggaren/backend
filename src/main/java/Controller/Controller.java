package Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import Entity.WikiAPI;
import org.springframework.boot.SpringApplication;

public class Controller {
    private WikiAPI wikiAPI = new WikiAPI();

    public Controller() {


    }

    @GetMapping("/api/biography/{artistName}")
    public String getBiography(@PathVariable String artistName) {
        String bio = WikiAPI.getBiographyFromWikiAPI(artistName);

        System.out.println(bio);

        return "Biography for " + artistName;
    }


}
