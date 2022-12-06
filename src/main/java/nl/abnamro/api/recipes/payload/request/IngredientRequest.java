package nl.abnamro.api.recipes.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.abnamro.api.recipes.domain.UnitOfMeasure;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class IngredientRequest {

    private String description;
    private BigDecimal amount;
    private UnitOfMeasure uom;

}
