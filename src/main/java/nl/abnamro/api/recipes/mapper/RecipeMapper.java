package nl.abnamro.api.recipes.mapper;

import nl.abnamro.api.recipes.domain.Recipe;
import nl.abnamro.api.recipes.payload.request.RecipeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {IngredientMapper.class})
public interface RecipeMapper {
    Recipe toEntity(RecipeRequest dto);
    RecipeRequest toDto(Recipe entity);

}
