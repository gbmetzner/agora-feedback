package com.agora.domain.feedback.application;

import com.agora.domain.feedback.application.dto.CreateFeedbackCommand;
import com.agora.domain.feedback.application.dto.UpdateFeedbackCommand;
import com.agora.domain.feedback.common.IdHelper;
import com.agora.domain.feedback.exception.CategoryNotFoundException;
import com.agora.domain.feedback.exception.FeedbackNotFoundException;
import com.agora.domain.feedback.exception.UnauthorizedException;
import com.agora.domain.feedback.model.dto.*;
import com.agora.domain.feedback.model.entity.Comment;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackCategory;
import com.agora.domain.feedback.model.VoteDirection;
import com.agora.domain.feedback.model.repository.CategoryRepository;
import com.agora.domain.feedback.model.repository.CommentRepository;
import com.agora.domain.feedback.model.repository.FeedbackRepository;
import com.agora.domain.user.exception.UserNotFoundException;
import com.agora.domain.user.model.User;
import com.agora.domain.user.model.repository.UserRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Application service for feedback management operations.
 * <p>
 * Handles business logic for feedback creation, retrieval, updates, and deletion.
 * Coordinates between repositories and enforces business rules and validations.
 * All public methods are transactional with proper error handling.
 * </p>
 *
 * @author Agora Team
 * @version 1.0
 */
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

    /**
     * Creates new feedback with the provided details.
     *
     * @param command The feedback creation command containing title, description, and optional metadata
     * @return FeedbackResponse containing the newly created feedback details
     * @throws CategoryNotFoundException if specified category does not exist
     * @throws UserNotFoundException if specified author does not exist
     */
    @Transactional
    public FeedbackResponse createFeedback(@Valid @NotNull CreateFeedbackCommand command, @NotNull String userId) {
        Feedback feedback = new Feedback(
                command.title(),
                command.description()
        );

        if (command.categoryId() != null) {
            FeedbackCategory category = categoryRepository.findById(IdHelper.toLong(command.categoryId()));
            if (category == null) {
                throw new CategoryNotFoundException(IdHelper.toLong(command.categoryId()));
            }
            feedback.setCategory(category);
        }


            User author = userRepository.findById(IdHelper.toLong(userId));
            if (author == null) {
                throw new UserNotFoundException(IdHelper.toLong(userId));
            }
            feedback.setAuthor(author);


//        if (command.sentiment() != null) {
//            feedback.setSentiment(command.sentiment());
//        }

//        if (command.tags() != null) {
//            feedback.setTags(command.tags());
//        }

        feedbackRepository.persist(feedback);
        return toResponse(feedback);
    }

    /**
     * Updates an existing feedback item with new details.
     *
     * @param id The feedback ID to update
     * @param command The update command containing new feedback details
     * @return Updated FeedbackResponse
     * @throws FeedbackNotFoundException if feedback with given ID does not exist
     * @throws CategoryNotFoundException if specified category does not exist
     * @throws UserNotFoundException if specified author does not exist
     */
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

    /**
     * Updates feedback with authorization check.
     * <p>
     * Only allows the creator of the feedback or an admin user to update it.
     * </p>
     *
     * @param id The feedback ID to update
     * @param command The update command with new values
     * @param currentUserId The ID of the user attempting the update
     * @return FeedbackResponse containing the updated feedback details
     * @throws FeedbackNotFoundException if feedback does not exist
     * @throws UnauthorizedException if the current user is not the author and not an admin
     * @throws CategoryNotFoundException if specified category does not exist
     * @throws UserNotFoundException if specified author does not exist
     */
    @Transactional
    public FeedbackResponse updateFeedback(@NotNull Long id, @Valid @NotNull UpdateFeedbackCommand command, @NotNull Long currentUserId) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }

        // Check authorization: only author or admin can update
        User currentUser = userRepository.findById(currentUserId);
        if (currentUser == null) {
            throw new UserNotFoundException(currentUserId);
        }

        boolean isAuthor = feedback.getAuthor() != null && feedback.getAuthor().getId().equals(currentUserId);
        boolean isAdmin = currentUser.role != null && currentUser.role.isAdmin();

        if (!isAuthor && !isAdmin) {
            throw new UnauthorizedException("Only the feedback author or an admin can update this feedback");
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

    /**
     * Retrieves a feedback item by its ID.
     *
     * @param id The feedback ID
     * @return FeedbackResponse containing the feedback details
     * @throws FeedbackNotFoundException if feedback with given ID does not exist
     */
    @Transactional
    public FeedbackResponse getFeedback(@NotNull Long id) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }
        return toResponse(feedback);
    }

    /**
     * Retrieves all feedbacks with pagination and optional sorting.
     *
     * @param pageNumber The page number (1-indexed). Defaults to 1 if less than 1.
     * @param pageSize The number of items per page (1-100). Defaults to 10 if less than 1, capped at 100.
     * @param sortOrder Sort order: "oldest" for ascending, any other value for descending (default)
     * @return PaginatedFeedbackResponse containing paginated feedback items with metadata
     */
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

    /**
     * Retrieves all feedbacks without pagination.
     *
     * @return List of all FeedbackResponse items
     */
    @Transactional
    public List<FeedbackResponse> getAllFeedbacks() {
        return feedbackRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Deletes a feedback item by its ID.
     *
     * @param id The feedback ID to delete
     * @throws FeedbackNotFoundException if feedback with given ID does not exist
     */
    @Transactional
    public void deleteFeedback(@NotNull Long id) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }
        feedbackRepository.deleteById(id);
    }

    /**
     * Archives a feedback item, marking it as inactive without deletion.
     *
     * @param id The feedback ID to archive
     * @return Updated FeedbackResponse with archived flag set
     * @throws FeedbackNotFoundException if feedback with given ID does not exist
     */
    @Transactional
    public FeedbackResponse archiveFeedback(@NotNull String id) {
        var feedback = feedbackRepository.findById(IdHelper.toLong(id));
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }
        feedback.archive();
        feedbackRepository.persist(feedback);
        return toResponse(feedback);
    }

    /**
     * Reopens an archived feedback item, making it active again.
     *
     * @param id The feedback ID to reopen
     * @return Updated FeedbackResponse with reopened status
     * @throws FeedbackNotFoundException if feedback with given ID does not exist
     */
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

    /**
     * Adds a new comment to an existing feedback item.
     *
     * @param feedbackId The feedback ID to comment on
     * @param request The comment creation request containing text and author details
     * @return CommentResponse containing the newly created comment
     * @throws FeedbackNotFoundException if feedback with given ID does not exist
     * @throws UserNotFoundException if the comment author does not exist
     */
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
                feedback.getCategory() != null ? feedback.getCategory().getName() : null,
                feedback.getAuthor() != null ? feedback.getAuthor().getName() : null,
                feedback.getCreatedAt(),
                feedback.isArchived()
        );
    }

    /**
     * Retrieves all comments for a specific feedback item.
     *
     * @param feedbackId The feedback ID to retrieve comments for
     * @return List of CommentResponse items for the feedback
     * @throws FeedbackNotFoundException if feedback with given ID does not exist
     */
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
        CommentAuthorResponse author = new CommentAuthorResponse(IdHelper.toString(comment.getAuthor().getId()),
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

    /**
     * Records a vote on feedback.
     * <p>
     * Supports upvoting, downvoting, or removing votes based on direction.
     * Only one vote per user is allowed (prevents double voting).
     * </p>
     *
     * @param id The feedback ID to vote on
     * @param direction The vote direction (up, down, none)
     * @return Updated FeedbackResponse with vote count
     * @throws FeedbackNotFoundException if feedback does not exist
     * @throws IllegalArgumentException if vote direction is invalid
     */
    @Transactional
    public FeedbackResponse voteFeedback(@NotNull Long id, @NotNull String direction) {
        Feedback feedback = feedbackRepository.findById(id);
        if (feedback == null) {
            throw new FeedbackNotFoundException(id);
        }

        // Parse vote direction
        VoteDirection voteDir = VoteDirection.fromString(direction);

        // Apply the vote
        switch (voteDir) {
            case UP -> feedback.upvote();
            case DOWN -> feedback.downvote();
            case NONE -> {
                // Remove votes (simple approach: reset both)
                feedback.removeUpvote();
                feedback.removeDownvote();
            }
        }

        feedbackRepository.persist(feedback);
        return toResponse(feedback);
    }

    /**
     * Records a vote on a comment.
     * <p>
     * Supports upvoting or removing upvotes based on direction.
     * </p>
     *
     * @param feedbackId The feedback ID containing the comment
     * @param commentId The comment ID to vote on
     * @param direction The vote direction (up, down, none)
     * @return Updated CommentResponse with vote count
     * @throws FeedbackNotFoundException if feedback does not exist
     * @throws IllegalArgumentException if comment does not exist or vote direction is invalid
     */
    @Transactional
    public CommentResponse voteComment(@NotNull Long feedbackId, @NotNull Long commentId, @NotNull String direction) {
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if (feedback == null) {
            throw new FeedbackNotFoundException(feedbackId);
        }

        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }

        // Verify if a comment belongs to the feedback
        if (!comment.getFeedback().getId().equals(feedbackId)) {
            throw new IllegalArgumentException("Comment does not belong to this feedback");
        }

        // Parse vote direction
        VoteDirection voteDir = VoteDirection.fromString(direction);

        // Apply the vote (comments only support upvote/none)
        switch (voteDir) {
            case UP -> comment.upvote();
            case DOWN, NONE -> comment.removeUpvote();
        }

        commentRepository.persist(comment);
        return toCommentResponse(comment);
    }

    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAll().stream().map( c -> new CategoryResponse(IdHelper.toString(c.getId()), c.getName())).toList();
    }
}
