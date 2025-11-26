package com.agora.domain.feedback.model.repository;

import com.agora.domain.feedback.model.entity.Comment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CommentRepository implements PanacheRepository<Comment> {
    public List<Comment> findByFeedbackId(Long feedbackId) {
        return find("feedback.id", feedbackId).list();
    }
}