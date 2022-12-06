package nl.abnamro.api.recipes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.Ingredient;
import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.domain.UnitOfMeasure;
import nl.abnamro.api.recipes.mapper.RecipeMapper;
import nl.abnamro.api.recipes.payload.request.IngredientRequest;
import nl.abnamro.api.recipes.payload.request.RecipeRequest;
import nl.abnamro.api.recipes.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class RecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RecipeService recipeService;

    @MockBean
    private RecipeMapper recipeMapper;
    @Autowired
    private ObjectMapper objectMapper;
    private RecipeRequest recipeRequest;
    private Recipe recipe;

    @MockBean
    private MappingMongoConverter mappingMongoConverter;

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

    @Test
    public void givenListOfRecipes_whenGetAllRecipes_thenReturnRecipesList() throws Exception{
        //given
        Recipe recipeTwo = Recipe.builder()
                .name("First Recipe")
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(Ingredient.builder().description("Sugar").amount(BigDecimal.valueOf(5)).uom(UnitOfMeasure.gr).build()).collect(Collectors.toSet()))
                .instructions("First step , Second step, Third step")
                .build();

        List<Recipe> recipeList=List.of(recipe,recipeTwo);
        given(recipeService.findAllRecipesBySearchParameters(any(),any(),any(),any(),any())).willReturn(recipeList);

        //when
        ResultActions actions = mockMvc.perform(get("/recipe").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(recipeRequest)));
        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(recipeList.size())))
                .andExpect(jsonPath("$.[0].description").value(recipe.getDescription()))
                .andExpect(jsonPath("$.[0].category").value(recipe.getCategory().name()))
                .andExpect(jsonPath("$.[0].servings").value(recipe.getServings()));

    }

    @Test
    void givenRecipeObject_whenCreateRecipe_thenReturnSavedRecipe() throws Exception {
        // given
        recipeRequest = RecipeRequest.builder()
                .name("First Recipe")
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(IngredientRequest.builder().description("Sugar").amount(BigDecimal.valueOf(5)).uom(UnitOfMeasure.gr).build()).collect(Collectors.toList()))
                .instructions("First step , Second step, Third step")
                .build();

        given(recipeService.addRecipe(any())).willReturn(recipe);

        // when
        ResultActions actions = mockMvc.perform(post("/recipe").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(recipeRequest)));
        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(recipeRequest.getName()))
                .andExpect(jsonPath("$.description").value(recipeRequest.getDescription()))
                .andExpect(jsonPath("$.category").value(recipeRequest.getCategory().name()))
                .andExpect(jsonPath("$.servings").value(recipeRequest.getServings()));

    }

    @Test
    public void givenUpdatedRecipe_whenUpdateRecipe_thenReturnUpdateRecipeObject() throws Exception {

        // given
        given(recipeService.updateRecipe(anyString(),any())).willReturn(recipe);

        // when
        ResultActions actions = mockMvc.perform(put("/recipe/"+ recipe.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(recipe)));
        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(recipe.getName()))
                .andExpect(jsonPath("$.description").value(recipe.getDescription()))
                .andExpect(jsonPath("$.category").value(recipe.getCategory().name()))
                .andExpect(jsonPath("$.servings").value(recipe.getServings()));
    }

    @Test
    public void givenRecipeId_whenDeleteRecipe_thenReturn200() throws Exception {
        // given
        String recipeId="aa";

        willDoNothing().given(recipeService).deleteRecipe(anyString());

        // when
        ResultActions actions = this.mockMvc.perform(delete("/recipe/" + recipeId));
        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Recipe has been deleted"))
                .andDo(print());
    }
}