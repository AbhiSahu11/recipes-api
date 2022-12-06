package nl.abnamro.api.recipes.repositories;

import nl.abnamro.api.recipes.domain.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RecipeRepository extends MongoRepository<Recipe, String>, QuerydslPredicateExecutor<Recipe> {

}
