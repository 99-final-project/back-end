package com.sparta.backend.domain.Recipe;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(exclude = "recipe")
@Getter
@NoArgsConstructor
@Entity
public class RecipeImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public RecipeImage(String image, Recipe recipe){
        this.image = image;
        this.recipe = recipe;
    }

    public RecipeImage updateRecipeImage(String image, Recipe recipe){
        this.image = image;
        this.recipe = recipe;
        return this;
    }
}
