package nl.abnamro.api.recipes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


//@JsonIgnoreProperties({"id","recipe"})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private Category category;
    private Integer servings;
    private String instructions;

    @OneToMany(mappedBy="recipe",cascade = CascadeType.ALL)
    private Set<Ingredient> ingredients=new HashSet<>();

}
