package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Tag extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

//    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private List<RecipeTag> recipeTagList;

    public Tag(String name, Recipe recipe){
        this.name = name;
        this.recipe = recipe;
    }
}
