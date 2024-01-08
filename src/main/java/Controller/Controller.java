package Controller;

import org.springframework.web.bind.annotation.*;
import Entity.WikiAPI;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "127.0.0.1:5500")
public class Controller {
    private WikiAPI wikiAPI = new WikiAPI();

    public Controller() {

    }

    @GetMapping("/api/test")
    public String getString() {
        return "Here is the string!";
    }

    @GetMapping("/api/biography/{artistName}")
    public String getBiography(@PathVariable("artistName") String artistName) {
        System.out.println(WikiAPI.getBiographyFromWikiAPI(artistName));
        return "Biography for " + artistName + ": " + WikiAPI.getBiographyFromWikiAPI(artistName);
    }


}
