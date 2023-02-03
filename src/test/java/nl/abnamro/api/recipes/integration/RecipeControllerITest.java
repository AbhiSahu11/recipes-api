package nl.abnamro.api.recipes.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.Ingredient;
import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.domain.UnitOfMeasure;
import nl.abnamro.api.recipes.mapper.RecipeMapper;
import nl.abnamro.api.recipes.payload.request.IngredientRequest;
import nl.abnamro.api.recipes.payload.request.RecipeRequest;
import nl.abnamro.api.recipes.repositories.IngredientRepository;
import nl.abnamro.api.recipes.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RecipeControllerITest {
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    RecipeMapper recipeMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;

    private RecipeRequest recipeRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        recipeRequest = RecipeRequest.builder()
                .name("First Recipe")
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(IngredientRequest.builder().description("Sugar").amount(Double.valueOf(5)).uom(UnitOfMeasure.gr).build()).collect(Collectors.toList()))
                .instructions("First step , Second step, Third step")
                .build();
    }


    @Test
    void givenRecipeObject_whenPostRecipe_thenReturnSavedRecipe() throws Exception {
        // given - recipeRequest is in setUp method

        // When
        ResultActions actions = this.mockMvc.perform(post("/recipe").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(recipeRequest)));
        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(recipeRequest.getName()))
                .andExpect(jsonPath("$.description").value(recipeRequest.getDescription()))
                .andExpect(jsonPath("$.category").value(recipeRequest.getCategory().name()))
                .andExpect(jsonPath("$.servings").value(recipeRequest.getServings()))
                .andExpect(jsonPath("$.ingredients", hasSize(recipeRequest.getIngredients().size())))
                .andExpect(jsonPath("$.ingredients[0].id").doesNotExist())
                .andExpect(jsonPath("$.ingredients[0].createdDate").doesNotExist())
                .andExpect(jsonPath("$.ingredients[0].lastModifiedDate").doesNotExist())
                .andExpect(jsonPath("$.ingredients[0].description").value(recipeRequest.getIngredients().get(0).getDescription()))
                .andExpect(jsonPath("$.ingredients[0].amount").value(recipeRequest.getIngredients().get(0).getAmount()))
                .andExpect(jsonPath("$.instructions").value(recipeRequest.getInstructions()))
                .andDo(print());
    }

    @Test
    void givenEmptyRecipeName_whenPostRecipe_thenReturnBadRequest() throws Exception {
        // given - name is not present in request
        recipeRequest = RecipeRequest.builder()
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(IngredientRequest.builder().description("Sugar").amount(Double.valueOf(5)).uom(UnitOfMeasure.gr).build()).collect(Collectors.toList()))
                .instructions("First step , Second step, Third step")
                .build();
        // When
        ResultActions actions = this.mockMvc.perform(post("/recipe").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(recipeRequest)));
        // Then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]",allOf(hasEntry("name", "must not be blank"))))
                .andDo(print());
    }

    @Test
    void givenUnknownCategory_whenPostRecipe_thenReturnBadRequest() throws Exception {
        // Given - recipeRequest is in setUp method
        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(recipeRequest));
        ((ObjectNode) json).put("category", "UNKNOWN_CATEGORY");

        // When
        ResultActions actions = this.mockMvc.perform(post("/recipe").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(json)));
        // Then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]",allOf(hasEntry("category", "value should be: NON_VEGETARIAN, VEGETARIAN"))));
    }

    @Test
    void givenUnknownUnitOfMeasure_whenPostRecipe_thenReturnBadRequest() throws Exception {
        // Given - recipeRequest is in setUp method
        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(recipeRequest));
        JsonNode ingredientJson = objectMapper.readTree(objectMapper.writeValueAsString(IngredientRequest.builder().description("Sugar").amount(Double.valueOf(5)).build()));
        ((ObjectNode) ingredientJson).put("uom", "gram");
        ArrayNode array = objectMapper.createArrayNode();
        array.add(ingredientJson);
        ((ObjectNode) json).putArray("ingredients").addAll(array);

        // When
        ResultActions actions = this.mockMvc.perform(post("/recipe").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(json)));
        // Then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]",allOf(hasEntry("ingredients", "value should be: tsp, gr, kg, cup, mL, tbsp, L"))))
                .andDo(print());
    }

    @Test
    void givenRecipeObject_whenPutRecipe_thenReturnUpdatedRecipe() throws Exception {
        // Given
        Recipe recipe = recipeRepository.save(Recipe.builder()
                .name("First Recipe")
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(ingredientRepository.save(Ingredient.builder().description("Sugar").amount(Double.valueOf(5)).uom(UnitOfMeasure.gr).build())).collect(Collectors.toSet()))
                .instructions("First step , Second step, Third step")
                .build());

        RecipeRequest recipeRequest = recipeMapper.toDto(recipe);
        recipeRequest.setName("First Recipe - Update");
        recipeRequest.setDescription("My first recipe - update");

        // When
        ResultActions actions = this.mockMvc.perform(put("/recipe/" + recipe.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(recipeRequest)));
        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(recipeRequest.getName()))
                .andExpect(jsonPath("$.description").value(recipeRequest.getDescription()))
                .andExpect(jsonPath("$.category").value(recipeRequest.getCategory().name()))
                .andExpect(jsonPath("$.servings").value(recipeRequest.getServings()))
                .andExpect(jsonPath("$.ingredients", hasSize(recipeRequest.getIngredients().size())))
                .andExpect(jsonPath("$.ingredients[0].description").value(recipeRequest.getIngredients().get(0).getDescription()))
                .andExpect(jsonPath("$.ingredients[0].amount").value(recipeRequest.getIngredients().get(0).getAmount()))
                .andExpect(jsonPath("$.instructions").value(recipeRequest.getInstructions()))
                .andDo(print())
        ;
    }

    @Test
    void givenInvalidRecipeId_whenPutRecipe_thenReturnBadRequest() throws Exception {
        // Given
        Recipe recipe = recipeRepository.save(Recipe.builder()
                .name("First Recipe")
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(ingredientRepository.save(Ingredient.builder().description("Sugar").amount(Double.valueOf(5)).uom(UnitOfMeasure.gr).build())).collect(Collectors.toSet()))
                .instructions("First step , Second step, Third step")
                .build());
        RecipeRequest recipeRequest = recipeMapper.toDto(recipe);
        recipeRequest.setName("First Recipe - Update");
        recipeRequest.setDescription("My first recipe - update");
        String invalidId = "aaa";

        // When
        ResultActions actions = this.mockMvc.perform(put("/recipe/" + invalidId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(recipeRequest)));
        // Then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("Can't found recipe by ID " + invalidId))
                .andExpect(jsonPath("$.errors").value("Can't found recipe by ID " + invalidId))
                .andDo(print());
    }

    @Test
    void givenRecipeId_whenDeleteRecipe_thenReturn200() throws Exception {
        // Given
        Recipe recipe = recipeRepository.save(Recipe.builder()
                .name("First Recipe")
                .description("My first recipe")
                .category(Category.VEGETARIAN)
                .servings(10)
                .ingredients(Stream.of(ingredientRepository.save(Ingredient.builder().description("Sugar").amount(Double.valueOf(5)).uom(UnitOfMeasure.gr).build())).collect(Collectors.toSet()))
                .instructions("First step , Second step, Third step")
                .build());

        // When
        ResultActions actions = this.mockMvc.perform(delete("/recipe/" + recipe.getId()));
        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Recipe has been deleted"))
                .andDo(print());
    }
    
    @Test
    void givenInvalidRecipeId_whenDeleteRecipe_thenReturnBadRequest() throws Exception {
        // Given
        String invalidId = "aaa";

        // When
        ResultActions actions = this.mockMvc.perform(delete("/recipe/" + invalidId));
        // Then
        actions
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("Can't found recipe by ID " + invalidId))
                .andExpect(jsonPath("$.errors").value("Can't found recipe by ID " + invalidId))
                .andDo(print());
    }

}
