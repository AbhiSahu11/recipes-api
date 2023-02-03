package nl.abnamro.api.recipes.repositories;


import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.Ingredient;
import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.domain.UnitOfMeasure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private Recipe recipe;

    @BeforeEach
    void setUp() {
        recipe = Recipe.builder()
                .name("First Recipe")
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(Ingredient.builder().description("Sugar").amount(Double.valueOf(5)).uom(UnitOfMeasure.gr).build()).collect(Collectors.toSet()))
                .instructions("First step , Second step, Third step")
                .build();
    }

    @AfterEach
    void cleanUpDatabase() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    public void givenRecipeObject_whenSave_thenReturnSavedRecipe(){
        // when
        recipe.getIngredients().stream().map(ingredientRepository::save).collect(Collectors.toList());
        Recipe savedRecipe = recipeRepository.save(recipe);

        // then
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getName()).isNotNull();
    }

    @Test
    public void givenRecipeList_whenFindAll_thenReturnRecipeList(){
        //given
        Recipe recipeTwo = Recipe.builder()
                .name("Second Recipe")
                .description("My Second recipe")
                .category(Category.NON_VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(Ingredient.builder().description("Meat").amount(Double.valueOf(5)).uom(UnitOfMeasure.kg).build()).collect(Collectors.toSet()))
                .instructions("First step , Second step, Third step")
                .build();

        recipe.getIngredients().stream().map(ingredientRepository::save).collect(Collectors.toList());
        recipeTwo.getIngredients().stream().map(ingredientRepository::save).collect(Collectors.toList());

        recipeRepository.save(recipe);
        recipeRepository.save(recipeTwo);

        // when
        List<Recipe> recipeList = recipeRepository.findAll();
        // then
        assertThat(recipeList).isNotNull();
        assertThat(recipeList.size()).isEqualTo(2);
    }

}
