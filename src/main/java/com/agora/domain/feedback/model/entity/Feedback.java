package com.agora.domain.feedback.model.entity;

import com.agora.domain.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "feedback")
public class Feedback extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Feedback(String title, String description, FeedbackStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = OffsetDateTime.now();
        this.archived = false;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public FeedbackCategory getCategory() {
        return category;
    }

    public User getAuthor() {
        return author;
    }

    public String getSentiment() {
        return sentiment;
    }

    public String getTags() {
        return tags;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isArchived() {
        return archived;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public void setCategory(FeedbackCategory category) {
        this.category = category;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    // Domain methods
    public void archive() {
        this.archived = true;
    }

    public void reopen() {
        if (this.status == FeedbackStatus.CLOSED) {
            this.status = FeedbackStatus.OPENED;
        }
    }

    public void changeSentiment(String newSentiment) {
        this.sentiment = newSentiment;
    }
}
