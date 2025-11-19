package com.agora.domain.feedback.model.repository;

import com.agora.domain.feedback.model.entity.FeedbackCategory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<FeedbackCategory> {
}
