package com.agora.domain.feedback;

import com.agora.domain.feedback.model.entity.Comment;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackCategory;
import com.agora.domain.feedback.model.entity.FeedbackStatus;
import com.agora.domain.user.model.User;

/**
 * Fluent builders for creating test entities.
 * <p>
 * Provides convenient factory methods to construct domain entities with sensible defaults
 * for use in tests. Builders follow the fluent builder pattern for readable test setup.
 * </p>
 */
public class TestDataBuilder {

    /**
     * Builder for creating Feedback entities in tests.
     */
    public static class FeedbackBuilder {
        private String title = "Test Feedback";
        private String description = "This is test feedback";
        private FeedbackStatus status = FeedbackStatus.PENDING;
        private FeedbackCategory category;
        private User author;
        private String sentiment;
        private int upvotes = 0;
        private int downvotes = 0;
        private int comments = 0;
        private String tags;
        private boolean archived = false;

        /**
         * Set the feedback title.
         *
         * @param title The feedback title
         * @return This builder for chaining
         */
        public FeedbackBuilder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the feedback description.
         *
         * @param description The feedback description
         * @return This builder for chaining
         */
        public FeedbackBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Set the feedback status.
         *
         * @param status The feedback status
         * @return This builder for chaining
         */
        public FeedbackBuilder status(FeedbackStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Set the feedback category.
         *
         * @param category The feedback category
         * @return This builder for chaining
         */
        public FeedbackBuilder category(FeedbackCategory category) {
            this.category = category;
            return this;
        }

        /**
         * Set the feedback author.
         *
         * @param author The feedback author
         * @return This builder for chaining
         */
        public FeedbackBuilder author(User author) {
            this.author = author;
            return this;
        }

        /**
         * Set the feedback sentiment.
         *
         * @param sentiment The feedback sentiment
         * @return This builder for chaining
         */
        public FeedbackBuilder sentiment(String sentiment) {
            this.sentiment = sentiment;
            return this;
        }

        /**
         * Set the upvotes count.
         *
         * @param upvotes The number of upvotes
         * @return This builder for chaining
         */
        public FeedbackBuilder upvotes(int upvotes) {
            this.upvotes = upvotes;
            return this;
        }

        /**
         * Set the downvotes count.
         *
         * @param downvotes The number of downvotes
         * @return This builder for chaining
         */
        public FeedbackBuilder downvotes(int downvotes) {
            this.downvotes = downvotes;
            return this;
        }

        /**
         * Set the comments count.
         *
         * @param comments The number of comments
         * @return This builder for chaining
         */
        public FeedbackBuilder comments(int comments) {
            this.comments = comments;
            return this;
        }

        /**
         * Set the feedback tags.
         *
         * @param tags The feedback tags
         * @return This builder for chaining
         */
        public FeedbackBuilder tags(String tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Set the archived flag.
         *
         * @param archived Whether the feedback is archived
         * @return This builder for chaining
         */
        public FeedbackBuilder archived(boolean archived) {
            this.archived = archived;
            return this;
        }

        /**
         * Build and return the Feedback entity.
         *
         * @return The constructed Feedback entity
         */
        public Feedback build() {
            Feedback feedback = new Feedback(title, description);
            feedback.setStatus(status);
            feedback.setCategory(category);
            feedback.setAuthor(author);
            feedback.setSentiment(sentiment);
            feedback.setUpvotes(upvotes);
            feedback.setDownvotes(downvotes);
            feedback.setComments(comments);
            feedback.setTags(tags);
            if (archived) {
                feedback.archive();
            }
            return feedback;
        }
    }

    /**
     * Builder for creating FeedbackCategory entities in tests.
     */
    public static class CategoryBuilder {
        private Long id;
        private String name = "Test Category";

        /**
         * Set the category ID.
         *
         * @param id The category ID
         * @return This builder for chaining
         */
        public CategoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Set the category name.
         *
         * @param name The category name
         * @return This builder for chaining
         */
        public CategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Build and return the FeedbackCategory entity.
         *
         * @return The constructed FeedbackCategory entity
         */
        public FeedbackCategory build() {
            if (id != null) {
                return new FeedbackCategory(id, name);
            }
            return new FeedbackCategory(name);
        }
    }

    /**
     * Builder for creating User entities in tests.
     */
    public static class UserBuilder {
        private String name = "Test User";
        private String username = "testuser_" + System.nanoTime();
        private String email = "test_" + System.nanoTime() + "@example.com";
        private Long discordId;
        private String discordUsername;

        /**
         * Set the user name.
         *
         * @param name The user name
         * @return This builder for chaining
         */
        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the user username.
         *
         * @param username The user username
         * @return This builder for chaining
         */
        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * Set the user email.
         *
         * @param email The user email
         * @return This builder for chaining
         */
        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Set the Discord ID.
         *
         * @param discordId The Discord ID
         * @return This builder for chaining
         */
        public UserBuilder discordId(Long discordId) {
            this.discordId = discordId;
            return this;
        }

        /**
         * Set the Discord username.
         *
         * @param discordUsername The Discord username
         * @return This builder for chaining
         */
        public UserBuilder discordUsername(String discordUsername) {
            this.discordUsername = discordUsername;
            return this;
        }

        /**
         * Build and return the User entity.
         *
         * @return The constructed User entity
         */
        public User build() {
            User user = new User();
            user.name = name;
            user.username = username;
            user.email = email;
            user.discordId = discordId;
            user.discordUsername = discordUsername;
            return user;
        }
    }

    /**
     * Builder for creating Comment entities in tests.
     */
    public static class CommentBuilder {
        private String text = "Test comment";
        private Feedback feedback;
        private User author;
        private boolean developerResponse = false;
        private int upvotes = 0;

        /**
         * Set the comment text.
         *
         * @param text The comment text
         * @return This builder for chaining
         */
        public CommentBuilder text(String text) {
            this.text = text;
            return this;
        }

        /**
         * Set the feedback for this comment.
         *
         * @param feedback The feedback entity
         * @return This builder for chaining
         */
        public CommentBuilder feedback(Feedback feedback) {
            this.feedback = feedback;
            return this;
        }

        /**
         * Set the comment author.
         *
         * @param author The author user
         * @return This builder for chaining
         */
        public CommentBuilder author(User author) {
            this.author = author;
            return this;
        }

        /**
         * Set whether this is a developer response.
         *
         * @param developerResponse Whether this is a developer response
         * @return This builder for chaining
         */
        public CommentBuilder developerResponse(boolean developerResponse) {
            this.developerResponse = developerResponse;
            return this;
        }

        /**
         * Set the upvotes count.
         *
         * @param upvotes The number of upvotes
         * @return This builder for chaining
         */
        public CommentBuilder upvotes(int upvotes) {
            this.upvotes = upvotes;
            return this;
        }

        /**
         * Build and return the Comment entity.
         *
         * @return The constructed Comment entity
         */
        public Comment build() {
            if (feedback == null || author == null) {
                throw new IllegalArgumentException("Comment requires feedback and author");
            }
            Comment comment = new Comment(text, feedback, author);
            comment.setDeveloperResponse(developerResponse);
            comment.setUpvotes(upvotes);
            return comment;
        }
    }

    /**
     * Create a new FeedbackBuilder instance.
     *
     * @return A new FeedbackBuilder
     */
    public static FeedbackBuilder feedback() {
        return new FeedbackBuilder();
    }

    /**
     * Create a new CategoryBuilder instance.
     *
     * @return A new CategoryBuilder
     */
    public static CategoryBuilder category() {
        return new CategoryBuilder();
    }

    /**
     * Create a new UserBuilder instance.
     *
     * @return A new UserBuilder
     */
    public static UserBuilder user() {
        return new UserBuilder();
    }

    /**
     * Create a new CommentBuilder instance.
     *
     * @return A new CommentBuilder
     */
    public static CommentBuilder comment() {
        return new CommentBuilder();
    }
}
