package nl.abnamro.api.recipes.repositories;

import nl.abnamro.api.recipes.domain.Ingredient;
import nl.abnamro.api.recipes.domain.UnitOfMeasure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class IngredientRepositoryTest {
    @Autowired
    private IngredientRepository ingredientRepository;

    private Ingredient ingredient;

    @BeforeEach
    public void setup(){
        ingredient = Ingredient.builder()
                .description("Meat")
                .amount(Double.valueOf(4))
                .uom(UnitOfMeasure.kg)
                .build();
    }
    @AfterEach
    void cleanUpDatabase() {
        ingredientRepository.deleteAll();
    }


    @Test
    public void givenIngredientObject_whenSave_thenReturnSavedIngredient(){
        // when
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        // then
        assertThat(savedIngredient).isNotNull();
        assertThat(savedIngredient.getAmount()).isEqualTo(ingredient.getAmount());
    }

    @Test
    public void givenIngredientList_whenFindAll_thenReturnIngredientList(){
        //given
        Ingredient ingredientTwo = Ingredient.builder()
                .description("Paneer")
                .amount(Double.valueOf(700))
                .uom(UnitOfMeasure.gr)
                .build();

        ingredientRepository.save(ingredient);
        ingredientRepository.save(ingredientTwo);

        // when
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        // then
        assertThat(ingredientList).isNotNull();
        assertThat(ingredientList.size()).isEqualTo(2);
    }

    @Test
    void givenIngredientObject_whenFindIngredientsByDescription_thenReturnSearchedIngredient() {
        // when
        ingredientRepository.save(ingredient);

        //when
        Ingredient searchedIngredient =ingredientRepository.findIngredientsByDescription(ingredient.getDescription());

        //then
        assertThat(searchedIngredient).isNotNull();
        assertThat(searchedIngredient.getDescription()).isEqualTo(ingredient.getDescription());
        assertThat(searchedIngredient.getAmount()).isEqualTo(ingredient.getAmount());
    }
}