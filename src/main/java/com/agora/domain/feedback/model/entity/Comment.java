package com.agora.domain.feedback.model.entity;

import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "comment")
@Setter
public class Comment extends PanacheEntityBase {

    @Id
    private Long id;

    @NotBlank(message = "Comment text cannot be blank")
    @Size(min = 1, max = 5000, message = "Comment must be between 1 and 5000 characters")
    private String text;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @NotNull(message = "Created timestamp cannot be null")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "is_developer_response")
    private boolean isDeveloperResponse;

    private int upvotes;

    public Comment() {
    }

    public Comment(String text, Feedback feedback, User author) {
        this.text = text;
        this.feedback = feedback;
        this.author = author;
    }

    @PrePersist
    public void prePersist() {
        this.id = IdHelper.generateId();
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.isDeveloperResponse = false;
        this.upvotes = 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}