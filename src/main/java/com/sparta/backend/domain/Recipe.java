package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Recipe extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recipe",cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Tag> tagList;

//    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private List<RecipeTag> recipeTagList;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Comment> commentList;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Likes> likesList;

    public Recipe(String title, String content, String image){
        this.title = title;
        this.content = content;
        this.image = image;
    }
    public Recipe updateRecipe(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;

        return this;
    }
}
