package nl.abnamro.api.recipes.service;

import com.querydsl.core.types.Predicate;
import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.Ingredient;
import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.domain.UnitOfMeasure;
import nl.abnamro.api.recipes.repositories.IngredientRepository;
import nl.abnamro.api.recipes.repositories.RecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private RecipeService recipeService;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        recipe = Recipe.builder()
                .name("First Recipe")
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(Ingredient.builder().description("Sugar").amount(BigDecimal.valueOf(5)).uom(UnitOfMeasure.gr).build()).collect(Collectors.toSet()))
                .instructions("First step , Second step, Third step")
                .build();
    }

    @AfterEach
    void cleanUpDatabase() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    @Test
    void givenRecipeObject_whenSaveRecipe_thenReturnRecipeObject() {
        // given
        given(recipeRepository.save(recipe)).willReturn(recipe);
        // when
        Recipe savedRecipe = recipeService.addRecipe(recipe);
        // then
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getName()).isEqualTo(recipe.getName());
        assertThat(savedRecipe.getServings()).isEqualTo(recipe.getServings());
        assertThat(savedRecipe.getDescription()).isEqualTo(recipe.getDescription());
    }

    @Test
    void givenRecipeObject_whenUpdateRecipe_thenReturnUpdatedRecipe() {
        // given
        given(recipeRepository.save(recipe)).willReturn(recipe);
        given(recipeRepository.findById(recipe.getId())).willReturn(Optional.of(recipe));

        recipe.setName("My Updated Recipe");
        recipe.setServings(4);

        // when
        Recipe updatedRecipe = recipeService.updateRecipe(recipe.getId(),recipe);

        // then
        assertThat(updatedRecipe).isNotNull();
        assertThat(updatedRecipe.getName()).isEqualTo("My Updated Recipe");
        assertThat(updatedRecipe.getServings()).isEqualTo(4);
    }

    @Test
    void givenRecipeId_whenDeleteRecipe_thenNothing() {
        //Given
        given(recipeRepository.findById(recipe.getId())).willReturn(Optional.of(recipe));
        willDoNothing().given(recipeRepository).delete(recipe);

        // when
        recipeService.deleteRecipe(recipe.getId());

        // then
        verify(recipeRepository, times(1)).delete(recipe);
    }

    @Test
    void givenRecipeList_whenFindAllRecipesBySearchParameters_thenReturnRecipeList() {
        Recipe recipeTwo = Recipe.builder()
                .name("Second Recipe")
                .description("My Second recipe")
                .category(Category.NON_VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(Ingredient.builder().description("Chicken").amount(BigDecimal.valueOf(5)).uom(UnitOfMeasure.kg).build()).collect(Collectors.toSet()))
                .instructions("First step , Second step, Third step")
                .build();

        Ingredient  ingredient = Ingredient.builder()
                .description("Meat")
                .amount(BigDecimal.valueOf(4))
                .uom(UnitOfMeasure.kg)
                .build();

        given(recipeRepository.findAll(any(Predicate.class))).willReturn(List.of(recipe,recipeTwo));
        given(ingredientRepository.findIngredientsByDescription(any())).willReturn(ingredient);

        //when
        List<Recipe> recipeList=recipeService.findAllRecipesBySearchParameters(Category.VEGETARIAN,2,"Meat","YES","First Instruction");
        //Then
        assertThat(recipeList).isNotNull();
        assertThat(recipeList.size()).isEqualTo(2);

    }
}