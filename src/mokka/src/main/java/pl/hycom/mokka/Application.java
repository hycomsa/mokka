package pl.hycom.mokka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Main class for spring-boot mokka ui application
 *
 * @author Michal Adamczyk, HYCOM S.A.
 */
@Controller
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @RequestMapping("/")
    String home() {
        return "welcome";
    }
}
