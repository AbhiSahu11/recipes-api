package nl.abnamro.api.recipes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.mapper.RecipeMapper;
import nl.abnamro.api.recipes.payload.request.RecipeRequest;
import nl.abnamro.api.recipes.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    @Operation(summary = "Get Recipe List based on search parameter(s)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Recipe List is retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - The request is not valid"),
    })
    @GetMapping
    public List<Recipe> getAllRecipes(@RequestParam(required = false) Category category,
                                      @RequestParam(required = false) Integer servings,
                                      @RequestParam(required = false) String ingredient,
                                      @RequestParam(required = false) String ingredientSelection,
                                      @RequestParam(required = false) String instruction) {

        return recipeService.findAllRecipesBySearchParameters(category, servings,ingredient,ingredientSelection,instruction);
    }

    @Operation(summary = "Create Recipe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Recipe is created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - The request is not valid"),
    })
    @PostMapping
    public Recipe createRecipe(@Valid @RequestBody RecipeRequest recipeRequest) {
        return recipeService.addRecipe(recipeMapper.toEntity(recipeRequest));
    }


    @Operation(summary = "Update Recipe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Recipe is updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - The request is not valid"),
    })
    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable String id, @Valid @RequestBody RecipeRequest recipeRequest) {
        return recipeService.updateRecipe(id, recipeMapper.toEntity(recipeRequest));
    }


    @Operation(summary = "Delete Recipe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Recipe is deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - The request is not valid"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity deleteRecipe(@PathVariable String id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Recipe has been deleted"));
    }


}
