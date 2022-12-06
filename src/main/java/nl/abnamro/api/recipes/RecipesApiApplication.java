package nl.abnamro.api.recipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class RecipesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipesApiApplication.class, args);
    }

}
