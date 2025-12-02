package com.agora.domain.feedback.fixture;

import com.agora.domain.feedback.model.entity.Comment;
import com.agora.domain.feedback.model.entity.Feedback;
import com.agora.domain.feedback.model.entity.FeedbackCategory;
import com.agora.domain.user.model.User;

/**
 * Fixtures for complete test scenarios.
 * <p>
 * Provides pre-configured complete scenarios combining feedback, categories,
 * users, and comments to represent realistic application states.
 * </p>
 */
public class ScenarioFixtures {

    public static class BugReportScenario {
        public final Feedback feedback;
        public final User reporter;
        public final User developer;
        public final Comment[] comments;

        public BugReportScenario() {
            this.reporter = UserFixtures.standardUser();
            this.developer = UserFixtures.developerUser();

            this.feedback = new Feedback(
                "Login button not responding",
                "When I click the login button, nothing happens"
            );
            this.feedback.setCategory(CategoryFixtures.bugReport());
            this.feedback.setAuthor(reporter);
            this.feedback.setSentiment("Negative");
            this.feedback.setUpvotes(8);
            this.feedback.setComments(3);

            this.comments = new Comment[]{
                new Comment(
                    "This happens to me too!",
                    feedback,
                    UserFixtures.standardUser()
                ),
                new Comment(
                    "We've identified the root cause and deployed a fix.",
                    feedback,
                    developer
                ),
                new Comment(
                    "Confirmed - the fix works! Thank you.",
                    feedback,
                    reporter
                )
            };
            this.comments[1].setDeveloperResponse(true);
            this.comments[1].setUpvotes(5);
        }
    }

    public static class FeatureRequestScenario {
        public final Feedback feedback;
        public final User requester;
        public final FeedbackCategory category;
        public final Comment[] comments;

        public FeatureRequestScenario() {
            this.requester = UserFixtures.standardUser();
            this.category = CategoryFixtures.featureRequest();

            this.feedback = new Feedback(
                "Add bulk export functionality",
                "Would like to export multiple items at once in CSV or JSON format"
            );
            this.feedback.setCategory(category);
            this.feedback.setAuthor(requester);
            this.feedback.setSentiment("Positive");
            this.feedback.setUpvotes(23);
            this.feedback.setComments(4);

            this.comments = new Comment[]{
                new Comment("I second this!", feedback, UserFixtures.standardUser()),
                new Comment("Great idea! This is on our roadmap.", feedback, UserFixtures.developerUser()),
                new Comment("What about Excel format too?", feedback, UserFixtures.standardUser()),
                new Comment("Yes, we can add that.", feedback, UserFixtures.developerUser())
            };
            this.comments[1].setDeveloperResponse(true);
            this.comments[3].setDeveloperResponse(true);
        }
    }

    public static class ComplexFeedbackScenario {
        public final Feedback[] feedback;
        public final User[] users;
        public final FeedbackCategory[] categories;

        public ComplexFeedbackScenario() {
            this.users = new User[]{
                UserFixtures.standardUser(),
                UserFixtures.adminUser(),
                UserFixtures.supportUser(),
                UserFixtures.developerUser()
            };

            this.categories = new FeedbackCategory[]{
                CategoryFixtures.bugReport(),
                CategoryFixtures.featureRequest(),
                CategoryFixtures.enhancement()
            };

            this.feedback = new Feedback[9];
            int index = 0;

            // 3 bug reports
            for (int i = 0; i < 3; i++) {
                Feedback f = new Feedback(
                    "Bug " + (i + 1),
                    "Description for bug " + (i + 1)
                );
                f.setCategory(categories[0]);
                f.setAuthor(users[i % users.length]);
                f.setSentiment("Negative");
                this.feedback[index++] = f;
            }

            // 3 feature requests
            for (int i = 0; i < 3; i++) {
                Feedback f = new Feedback(
                    "Feature " + (i + 1),
                    "Description for feature " + (i + 1)
                );
                f.setCategory(categories[1]);
                f.setAuthor(users[i % users.length]);
                f.setSentiment("Positive");
                this.feedback[index++] = f;
            }

            // 3 enhancements
            for (int i = 0; i < 3; i++) {
                Feedback f = new Feedback(
                    "Enhancement " + (i + 1),
                    "Description for enhancement " + (i + 1)
                );
                f.setCategory(categories[2]);
                f.setAuthor(users[i % users.length]);
                f.setSentiment("Neutral");
                this.feedback[index++] = f;
            }
        }
    }

    public static class PaginationScenario {
        public final Feedback[] feedbackItems;
        public final int totalCount;

        public PaginationScenario() {
            this.totalCount = 25;
            this.feedbackItems = new Feedback[totalCount];

            for (int i = 0; i < totalCount; i++) {
                Feedback f = new Feedback(
                    "Feedback " + (i + 1),
                    "Description for feedback item " + (i + 1)
                );
                f.setCategory(
                    i % 3 == 0 ? CategoryFixtures.bugReport() :
                    i % 3 == 1 ? CategoryFixtures.featureRequest() :
                    CategoryFixtures.enhancement()
                );
                f.setAuthor(UserFixtures.standardUser());
                f.setUpvotes(i);
                this.feedbackItems[i] = f;
            }
        }
    }

    public static class ArchiveScenario {
        public final Feedback activeFeedback;
        public final Feedback archivedFeedback;
        public final User user;

        public ArchiveScenario() {
            this.user = UserFixtures.standardUser();

            this.activeFeedback = new Feedback("Active Issue", "Currently being worked on");
            this.activeFeedback.setAuthor(user);
            this.activeFeedback.setCategory(CategoryFixtures.bugReport());

            this.archivedFeedback = new Feedback("Archived Issue", "No longer relevant");
            this.archivedFeedback.setAuthor(user);
            this.archivedFeedback.setCategory(CategoryFixtures.other());
            this.archivedFeedback.archive();
        }
    }

    // ===== FACTORY METHODS =====

    public static BugReportScenario bugReportScenario() {
        return new BugReportScenario();
    }

    public static FeatureRequestScenario featureRequestScenario() {
        return new FeatureRequestScenario();
    }

    public static ComplexFeedbackScenario complexScenario() {
        return new ComplexFeedbackScenario();
    }

    public static PaginationScenario paginationScenario() {
        return new PaginationScenario();
    }

    public static ArchiveScenario archiveScenario() {
        return new ArchiveScenario();
    }
}
