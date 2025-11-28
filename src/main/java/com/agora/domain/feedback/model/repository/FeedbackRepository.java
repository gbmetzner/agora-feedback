package com.agora.domain.feedback.model.repository;

import com.agora.domain.feedback.model.entity.Feedback;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for Feedback entity persistence operations.
 * <p>
 * Provides CRUD operations and query methods for Feedback entities using Quarkus Panache ORM.
 * Inherits standard operations like findById, persist, delete from PanacheRepository.
 * </p>
 *
 * @author Agora Team
 * @version 1.0
 * @see Feedback
 */
@ApplicationScoped
public class FeedbackRepository implements PanacheRepository<Feedback> {
}
