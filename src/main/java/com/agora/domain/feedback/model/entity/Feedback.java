package com.agora.domain.feedback.model.entity;

import com.agora.domain.feedback.common.IdGenerator;
import com.agora.domain.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

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
    private FeedbackStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private FeedbackCategory category;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Size(max = 50, message = "Sentiment must not exceed 50 characters")
    private String sentiment;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    @NotNull(message = "Created timestamp cannot be null")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @NotNull(message = "Archived flag cannot be null")
    private boolean archived;

    public Feedback() {
    }

    public Feedback(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdAt = OffsetDateTime.now();
        this.archived = false;
        this.status = FeedbackStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        this.id = IdGenerator.generateId();
    }

    // Domain methods
    public void archive() {
        this.archived = true;
    }

    public void reopen() {
        if (this.status == FeedbackStatus.COMPLETED) {
            this.status = FeedbackStatus.PENDING;
        }
    }

    public void changeSentiment(String newSentiment) {
        this.sentiment = newSentiment;
    }
}
