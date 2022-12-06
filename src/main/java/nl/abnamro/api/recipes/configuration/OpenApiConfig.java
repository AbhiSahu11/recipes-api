package nl.abnamro.api.recipes.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI RecipesAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Recipes API")
                        .description("This is a API Implementation for the demonstration of Recipes Using Spring Boot 2 .")
                        .version("v1.0"));

    }

}
