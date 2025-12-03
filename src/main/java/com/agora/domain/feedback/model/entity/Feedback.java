package com.agora.domain.feedback.model.entity;

import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "feedback")
@Setter
public class Feedback extends PanacheEntityBase {

    @Id
    private Long id;

    @NotBlank(message = "Feedback title cannot be blank")
    @Size(min = 3, max = 255, message = "Feedback title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "Feedback description cannot be blank")
    @Size(min = 10, max = 5000, message = "Feedback description must be between 10 and 5000 characters")
    private String description;

    @NotNull(message = "Feedback status cannot be null")
    @Enumerated(EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    private FeedbackStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private FeedbackCategory category;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Size(max = 50, message = "Sentiment must not exceed 50 characters")
    private String sentiment;

    private int upvotes;
    private int downvotes;
    private int comments;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    @NotNull(message = "Created timestamp cannot be null")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @NotNull(message = "Updated timestamp cannot be null")
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @NotNull(message = "Archived flag cannot be null")
    private boolean archived;

    public Feedback() {
    }

    public Feedback(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @PrePersist
    public void prePersist() {
        this.id = IdHelper.generateId();
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.archived = false;
        this.status = FeedbackStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Domain methods
    public void archive() {
        this.archived = true;
    }

    public void reopen() {
        if (this.status == FeedbackStatus.COMPLETED) {
            this.status = FeedbackStatus.PENDING;
        }
        this.archived = false;
    }

    public void changeSentiment(String newSentiment) {
        this.sentiment = newSentiment;
    }

    /**
     * Record an upvote on this feedback.
     * Increments the upvotes counter.
     */
    public void upvote() {
        this.upvotes++;
    }

    /**
     * Remove an upvote from this feedback.
     * Decrements the upvotes counter, minimum 0.
     */
    public void removeUpvote() {
        if (this.upvotes > 0) {
            this.upvotes--;
        }
    }

    /**
     * Record a downvote on this feedback.
     * Increments the downvotes counter.
     */
    public void downvote() {
        this.downvotes++;
    }

    /**
     * Remove a downvote from this feedback.
     * Decrements the downvotes counter, minimum 0.
     */
    public void removeDownvote() {
        if (this.downvotes > 0) {
            this.downvotes--;
        }
    }
}
