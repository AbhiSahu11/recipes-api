package nl.abnamro.api.recipes.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.abnamro.api.recipes.domain.Category;
import javax.validation.constraints.NotBlank;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RecipeRequest {

    @NotBlank
    private String name;
    private String description;
    private Category category;
    private Integer servings;
    private List<IngredientRequest> ingredients;
    private String instructions;

}
