package nl.abnamro.api.recipes.repositories;


import nl.abnamro.api.recipes.domain.Category;
import nl.abnamro.api.recipes.domain.Ingredient;
import nl.abnamro.api.recipes.domain.Recipe;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

import static nl.abnamro.api.recipes.util.Util.checkValidInput;

@Component
public class RecipesSpecification {

    public Specification<Recipe> getRecipes(Category category, String servings, String ingredient, String ingredientSelection, String instruction){
        return (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (checkValidInput(String.valueOf(category))) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            if (checkValidInput(servings)) {
                predicates.add(criteriaBuilder.equal(root.get("servings"), servings));
            }
            if (checkValidInput(ingredient)) {
                predicates.add(criteriaBuilder.equal(root.get("ingredient"), ingredient));
            }
            if (checkValidInput(instruction)) {
                predicates.add(criteriaBuilder.equal(root.get("instruction"), instruction));
            }
           // criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
