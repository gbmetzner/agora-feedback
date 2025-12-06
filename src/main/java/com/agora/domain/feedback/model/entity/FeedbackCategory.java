package com.agora.domain.feedback.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Entity
@Table(name = "category")
public class FeedbackCategory extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    public FeedbackCategory() {
    }

    public FeedbackCategory(String name) {
        this.name = name;
    }

    public FeedbackCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
