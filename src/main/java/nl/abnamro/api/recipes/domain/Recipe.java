package nl.abnamro.api.recipes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "Recipe")
@JsonIgnoreProperties({"createdBy", "lastModifiedBy"})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Recipe extends AbstractBaseEntity {

    private String name;
    private String description;
    private Category category;
    private Integer servings;

    @DBRef
    private Set<Ingredient> ingredients = new HashSet<>();

    private String instructions;

}
