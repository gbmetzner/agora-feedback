package com.agora.domain.feedback.application;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.application.dto.UpdateFeedbackCommand;
import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.feedback.exception.CategoryNotFoundException;
import com.agora.domain.feedback.exception.FeedbackNotFoundException;
import com.agora.domain.feedback.model.dto.CommentAuthorResponse;
import com.agora.domain.feedback.model.dto.CommentResponse;
import com.agora.domain.feedback.model.dto.CreateCommentRequest;
import com.agora.domain.feedback.model.dto.FeedbackResponse;
import com.agora.domain.feedback.model.dto.PaginatedFeedbackResponse;
import com.agora.domain.feedback.model.entity.Comment;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackCategory;
import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.agora.domain.feedback.model.repository.CategoryRepository;
import com.agora.domain.feedback.model.repository.CommentRepository;
import com.agora.domain.feedback.model.repository.FeedbackRepository;
import com.agora.domain.user.exception.UserNotFoundException;
import com.agora.domain.user.model.User;
import com.agora.domain.user.model.repository.UserRepository;
import io.hypersistence.tsid.TSID;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
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
    private final CommentRepository commentRepository;

    @Inject
    public FeedbackApplicationService(FeedbackRepository feedbackRepository,
                                       CategoryRepository categoryRepository,
                                       UserRepository userRepository,
                                       CommentRepository commentRepository) {
        this.feedbackRepository = feedbackRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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
    public PaginatedFeedbackResponse getAllFeedbacksPaginated(int pageNumber, int pageSize, String sortOrder) {
        // Validate inputs
        int page = Math.max(1, pageNumber);
        int size = Math.max(1, Math.min(pageSize, 100)); // Max 100 items per page

        // Determine sort direction based on sortOrder parameter
        Sort.Direction direction = "oldest".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.Ascending
                : Sort.Direction.Descending;

        Sort sort = Sort.by("createdAt", direction);

        // Get paginated results
        var pageResult = feedbackRepository.findAll(sort).page(Page.of(page - 1, size)).list();
        long totalItems = feedbackRepository.count();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        List<FeedbackResponse> items = pageResult.stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedFeedbackResponse(items, page, size, totalItems, totalPages);
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

    @Transactional
    public CommentResponse addComment(@NotNull Long feedbackId, @Valid @NotNull CreateCommentRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if (feedback == null) {
            throw new FeedbackNotFoundException(feedbackId);
        }

        User author = userRepository.findById(117457749108987389L); //request.authorId()
        if (author == null) {
            throw new UserNotFoundException(1L);
        }

        Comment comment = new Comment(request.text(), feedback, author);
        commentRepository.persist(comment);

        feedback.setComments(feedback.getComments() + 1);
        feedbackRepository.persist(feedback);

        return toCommentResponse(comment);
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        return new FeedbackResponse(IdHelper.toString(feedback.getId()),
                feedback.getTitle(),
                feedback.getDescription(),
                feedback.getSentiment(),
                feedback.getUpvotes(),
                feedback.getComments(),
                feedback.getStatus(),
                feedback.getCategory().getName(),
                feedback.getAuthor().getName(),
                feedback.getCreatedAt(),
                feedback.isArchived()
        );
    }

    @Transactional
    public List<CommentResponse> getCommentsByFeedbackId(@NotNull Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if (feedback == null) {
            throw new FeedbackNotFoundException(feedbackId);
        }

        return commentRepository.findByFeedbackId(feedbackId)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    private CommentResponse toCommentResponse(Comment comment) {
        CommentAuthorResponse author = new CommentAuthorResponse(
                IdHelper.toString(comment.getAuthor().getId()),
                comment.getAuthor().getName()
        );

        return new CommentResponse(
                IdHelper.toString(comment.getId()),
                author,
                comment.getText(),
                comment.isDeveloperResponse(),
                comment.getUpvotes(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
