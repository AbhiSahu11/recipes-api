package nl.abnamro.api.recipes.repositories;

import nl.abnamro.api.recipes.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, String> {

    Ingredient findIngredientsByDescription(String description);
}
