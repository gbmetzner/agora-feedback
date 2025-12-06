package com.agora.domain.feedback.model.repository;

import com.agora.domain.feedback.model.entity.Comment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository for Comment entity persistence operations.
 * <p>
 * Provides CRUD operations and query methods for Comment entities using Quarkus Panache ORM.
 * Inherits standard operations like findById, persist, delete from PanacheRepository.
 * Includes custom queries for feedback-specific comment retrieval.
 * </p>
 *
 * @author Agora Team
 * @version 1.0
 * @see Comment
 */
@ApplicationScoped
public class CommentRepository implements PanacheRepository<Comment> {
    /**
     * Finds all comments associated with a specific feedback item.
     *
     * @param feedbackId The feedback ID to retrieve comments for
     * @return List of comments for the feedback, or empty list if none found
     */
    public List<Comment> findByFeedbackId(Long feedbackId) {
        return find("feedback.id", feedbackId).list();
    }
}
