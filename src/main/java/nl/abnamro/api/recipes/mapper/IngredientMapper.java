package nl.abnamro.api.recipes.mapper;

import nl.abnamro.api.recipes.domain.Ingredient;
import nl.abnamro.api.recipes.payload.request.IngredientRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IngredientMapper {
    Ingredient toEntity(IngredientRequest dto);

}
