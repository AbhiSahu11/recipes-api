package nl.abnamro.api.recipes.repositories;

import nl.abnamro.api.recipes.domain.Recipe;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findAll(Specification<Recipe> specification);

}
