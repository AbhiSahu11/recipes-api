package nl.abnamro.api.recipes.repositories;

import nl.abnamro.api.recipes.domain.Ingredient;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IngredientRepository extends MongoRepository<Ingredient, String> {

    Ingredient findIngredientsByDescription(String description);
}
