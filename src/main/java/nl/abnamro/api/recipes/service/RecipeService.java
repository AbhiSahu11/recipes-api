package nl.abnamro.api.recipes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.Ingredient;
import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.repositories.IngredientRepository;
import nl.abnamro.api.recipes.repositories.RecipeRepository;
import nl.abnamro.api.recipes.repositories.RecipesSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.webjars.NotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    private final RecipesSpecification recipesSpecification;

    @Transactional
    public Recipe addRecipe(Recipe recipe) {

        recipeRepository.save(recipe);

        if (!CollectionUtils.isEmpty(recipe.getIngredients())) {
            recipe.getIngredients().stream().peek(ingredient -> ingredient.setRecipe(recipe))
                    .collect(Collectors.toSet()).stream().map(ingredientRepository::save);
        }
        return recipe;
    }

    public Recipe updateRecipe(String id, Recipe recipe) {

        Recipe oldRecipe = recipeRepository.findById(Long.valueOf(id))
                                            .orElseThrow(() -> new NotFoundException(String.format("Can't found recipe by ID %s", id)));

        BeanUtils.copyProperties(recipe, oldRecipe, "id", "ingredients", "createdDate");
        oldRecipe.getIngredients().clear();

        if (!CollectionUtils.isEmpty(recipe.getIngredients())) {
            oldRecipe.setIngredients(recipe.getIngredients().stream().map(ingredientRepository::save).collect(Collectors.toSet()));
        }

        return  recipeRepository.save(oldRecipe);
    }

    public void deleteRecipe(String id) {
        recipeRepository.delete(recipeRepository.findById(Long.valueOf(id))
                                                .orElseThrow(() -> new NotFoundException(String.format("Can't found recipe by ID %s", id))));
    }

    public List<Recipe> findAllRecipesBySearchParameters(Category category, String servings, String ingredient,String ingredientSelection, String instruction) {

            return recipeRepository.findAll(recipesSpecification.getRecipes(category, servings, ingredient,ingredientSelection, instruction))
                            .stream()
                            .sorted(Comparator.comparing(Recipe::getId)).collect(Collectors.toList());
    }



}
