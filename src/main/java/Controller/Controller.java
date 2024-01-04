package Controller;


import Entity.wikiAPI;

public class Controller {
    private wikiAPI wikiAPI = new wikiAPI();

    public Controller() {
        String ans = getBiography("test");
        System.out.println(ans);
    }

    public String getBiography(String personName) {
        return wikiAPI.getBiographyFromWikiAPI();
    }
}
