package com.agora.domain.feedback.application;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.application.dto.UpdateFeedbackCommand;
import com.agora.domain.feedback.exception.CategoryNotFoundException;
import com.agora.domain.feedback.exception.FeedbackNotFoundException;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackCategory;
import com.agora.domain.feedback.model.repository.CategoryRepository;
import com.agora.domain.feedback.model.repository.FeedbackRepository;
import com.agora.domain.user.exception.UserNotFoundException;
import com.agora.domain.user.model.User;
import com.agora.domain.user.model.repository.UserRepository;
import io.hypersistence.tsid.TSID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@ApplicationScoped
public class FeedbackApplicationService {
    private final FeedbackRepository feedbackRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Inject
    public FeedbackApplicationService(FeedbackRepository feedbackRepository,
                                       CategoryRepository categoryRepository,
                                       UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FeedbackResponse createFeedback(@Valid @NotNull CreateFeedbackCommand command) {
        Feedback feedback = new Feedback(
                command.title(),
                command.description()
        );

        if (command.categoryId() != null) {
            FeedbackCategory category = categoryRepository.findById(command.categoryId());
            if (category == null) {
                throw new CategoryNotFoundException(command.categoryId());
            }
            feedback.setCategory(category);
        }

        if (command.authorId() != null) {
            User author = userRepository.findById(command.authorId());
            if (author == null) {
                throw new UserNotFoundException(command.authorId());
            }
            feedback.setAuthor(author);
        }

        if (command.sentiment() != null) {
            feedback.setSentiment(command.sentiment());
        }

        if (command.tags() != null) {
            feedback.setTags(command.tags());
        }

        feedbackRepository.persist(feedback);
        return toResponse(feedback);
    }

    @Transactional
    public FeedbackResponse updateFeedback(@NotNull Long id, @Valid @NotNull UpdateFeedbackCommand command) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }

        feedback.setTitle(command.title());
        feedback.setDescription(command.description());
        feedback.setStatus(command.status());

        if (command.categoryId() != null) {
            FeedbackCategory category = categoryRepository.findById(command.categoryId());
            if (category == null) {
                throw new CategoryNotFoundException(command.categoryId());
            }
            feedback.setCategory(category);
        } else {
            feedback.setCategory(null);
        }

        if (command.authorId() != null) {
            User author = userRepository.findById(command.authorId());
            if (author == null) {
                throw new UserNotFoundException(command.authorId());
            }
            feedback.setAuthor(author);
        } else {
            feedback.setAuthor(null);
        }

        feedback.setSentiment(command.sentiment());
        feedback.setTags(command.tags());

        feedbackRepository.persist(feedback);
        return toResponse(feedback);
    }

    @Transactional
    public FeedbackResponse getFeedback(@NotNull Long id) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }
        return toResponse(feedback);
    }

    @Transactional
    public List<FeedbackResponse> getAllFeedbacks() {
        return feedbackRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteFeedback(@NotNull Long id) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }
        feedbackRepository.deleteById(id);
    }

    @Transactional
    public FeedbackResponse archiveFeedback(@NotNull Long id) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }
        feedback.archive();
        feedbackRepository.persist(feedback);
        return toResponse(feedback);
    }

    @Transactional
    public FeedbackResponse reopenFeedback(@NotNull Long id) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }
        feedback.reopen();
        feedbackRepository.persist(feedback);
        return toResponse(feedback);
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        return new FeedbackResponse(
                TSID.from(feedback.getId()).toString(),
                feedback.getTitle(),
                feedback.getDescription(),
                feedback.getStatus(),
                feedback.getCategory() != null ? feedback.getCategory().getId() : null,
                feedback.getCategory() != null ? feedback.getCategory().getName() : null,
                feedback.getAuthor() != null ? feedback.getAuthor().getId() : null,
                feedback.getAuthor() != null ? feedback.getAuthor().getName() : null,
                feedback.getSentiment(),
                feedback.getTags(),
                feedback.getCreatedAt(),
                feedback.isArchived()
        );
    }
}
