package nl.abnamro.api.recipes.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@JsonIgnoreProperties({"id","createdDate", "lastModifiedDate", "createdBy", "lastModifiedBy"})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private UnitOfMeasure uom;
    @ManyToOne(fetch = FetchType.LAZY,targetEntity = Recipe.class)
    @JsonIgnore
    private Recipe recipe;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient )) return false;
        return id != null && id.equals(((Ingredient) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
