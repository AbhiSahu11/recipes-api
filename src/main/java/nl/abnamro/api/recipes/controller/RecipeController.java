package nl.abnamro.api.recipes.controller;

import lombok.RequiredArgsConstructor;
import nl.abnamro.api.recipes.common.ApiResponse;
import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.mapper.RecipeMapper;
import nl.abnamro.api.recipes.payload.request.RecipeRequest;
import nl.abnamro.api.recipes.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllRecipes(@RequestParam(required = false) Category category,
                                      @RequestParam(required = false) String servings,
                                      @RequestParam(required = false) String ingredient,
                                      @RequestParam(required = false) String ingredientSelection,
                                      @RequestParam(required = false) String instruction) {

        List<Recipe> recipes= recipeService.findAllRecipesBySearchParameters(category, servings,ingredient,ingredientSelection,instruction);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse( "Recipe List is received",recipes));

    }

    @PostMapping
    public ResponseEntity<ApiResponse> createRecipe(@Valid @RequestBody RecipeRequest recipeRequest) {
        Recipe recipe= recipeService.addRecipe(recipeMapper.toEntity(recipeRequest));
        return ResponseEntity.status(HttpStatus.CREATED) .body(new ApiResponse( "Successfully created recipe!",recipe));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRecipe(@PathVariable String id, @Valid @RequestBody RecipeRequest recipeRequest) {
        Recipe recipe=recipeService.updateRecipe(id, recipeMapper.toEntity(recipeRequest));
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse( "Successfully updated recipe!",recipe));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity deleteRecipe(@PathVariable String id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted recipe!");
    }


}
