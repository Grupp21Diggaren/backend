package Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Entity.WikiAPI;

@RestController
//@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controller {
    private WikiAPI wikiAPI = new WikiAPI();

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


}
