package nl.abnamro.api.recipes.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.QRecipe;
import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.repositories.IngredientRepository;
import nl.abnamro.api.recipes.repositories.RecipeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public Recipe addRecipe(Recipe recipe) {

        if (! CollectionUtils.isEmpty(recipe.getIngredients()))
            recipe.setIngredients(recipe.getIngredients().stream().map(ingredientRepository::save).collect(Collectors.toSet()));

        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(String id, Recipe recipe) {

        Recipe oldRecipe = recipeRepository.findById(id)
                                            .orElseThrow(() -> new NotFoundException(String.format("Can't found recipe by ID %s", id)));

        BeanUtils.copyProperties(recipe, oldRecipe, "id", "ingredients", "createdDate");
        oldRecipe.getIngredients().clear();

        if (! CollectionUtils.isEmpty(recipe.getIngredients())) {
            oldRecipe.setIngredients(recipe.getIngredients().stream().map(ingredientRepository::save).collect(Collectors.toSet()));
        }

        return recipeRepository.save(oldRecipe);
    }

    public void deleteRecipe(String id) {
        recipeRepository.delete(recipeRepository.findById(id)
                                                .orElseThrow(() -> new NotFoundException(String.format("Can't found recipe by ID %s", id))));
    }

    public List<Recipe> findAllRecipesBySearchParameters(Category category, Integer servings, String ingredient,String ingredientSelection, String instruction) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (category != null) {
            booleanBuilder.and(QRecipe.recipe.category.eq(category));
        }
        if (servings != null) {
            booleanBuilder.and(QRecipe.recipe.servings.eq(servings));
        }
        if (ingredient != null && (ingredientSelection!=null && ingredientSelection.equalsIgnoreCase("YES"))) {
            booleanBuilder.and(QRecipe.recipe.ingredients.contains(ingredientRepository.findIngredientsByDescription(ingredient)));
        } else {
            booleanBuilder.and(QRecipe.recipe.ingredients.contains(ingredientRepository.findIngredientsByDescription(ingredient)).not());
        }
        if(instruction !=null) {
            booleanBuilder.and(QRecipe.recipe.instructions.containsIgnoreCase(instruction));
        }

        return  booleanBuilder.getValue() == null ?
                recipeRepository.findAll() :
                StreamSupport.stream(recipeRepository.findAll(booleanBuilder.getValue()).spliterator(), false)
                        .collect(Collectors.toList());
    }



}
