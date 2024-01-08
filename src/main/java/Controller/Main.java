package Controller;

import Entity.WikiAPI;
import org.springframework.boot.SpringApplication;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        SpringApplication.run(WikiAPI.class, args);
    }
}
