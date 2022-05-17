package com.geekhub.feedback;

import com.geekhub.exception.FeedbackNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {FeedbackRepository.class, FeedbackMapper.class})
@Sql(scripts = "classpath:schema.sql")
class FeedbackRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    FeedbackMapper feedbackMapper;

    @Autowired
    FeedbackRepository feedbackRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE feedback");
    }

    @Test
    void no_history_records_in_db() {
        long feedbackCount = feedbackRepository.showAll().size();
        assertThat(feedbackCount).isZero();
    }

    @Test
    void nothing_happened_when_trying_to_delete_not_existing_feedback() {
        assertThatCode(() -> feedbackRepository.delete(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DirtiesContext
    void check_return_right_size_all_feedbacks_after_add_new() {
        assertThat(feedbackRepository.showAll().size()).isEqualTo(0);

        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L, "user",
                "feedback", 1);

        feedbackRepository.create(feedback);

        assertThat(feedbackRepository.showAll().size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void check_return_feedbacks_by_movieId() {
        assertThat(feedbackRepository.showAllByFilmId(1).size()).isEqualTo(0);

        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L, "user",
                "feedback", 1);
        Feedback feedback1 = new Feedback(2L, LocalDateTime.now(), 2, 1L, "user",
                "feedback", 1);

        feedbackRepository.create(feedback);
        feedbackRepository.create(feedback1);

        assertThat(feedbackRepository.showAllByFilmId(1).size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void check_return_feedbacks_by_movieId_when_check_return_2_movies() {
        assertThat(feedbackRepository.showAllByFilmId(1).size()).isEqualTo(0);

        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L, "user",
                "feedback", 1);
        Feedback feedback1 = new Feedback(2L, LocalDateTime.now(), 1, 1L, "user",
                "feedback", 1);

        feedbackRepository.create(feedback);
        feedbackRepository.create(feedback1);

        assertThat(feedbackRepository.showAllByFilmId(1).size()).isEqualTo(2);
    }

    @Test
    void getFeedback_when_feedback_was_not_found() {
        assertThrows(FeedbackNotFoundException.class, () -> feedbackRepository.getById(1L));
    }

    @Test
    @DirtiesContext
    void getFeedback_check_return_right_feedback() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L, "user",
                "check feedback method", 1);

        feedbackRepository.create(feedback);
        Feedback feedbackFromDb = feedbackRepository.getById(1L);

        assertThat(feedbackFromDb).extracting(Feedback::getFeedback).isEqualTo(feedback.getFeedback());
    }

    @Test
    @DirtiesContext
    void check_method_delete() {
        assertThat(feedbackRepository.showAll().size()).isEqualTo(0);

        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L, "user",
                "check feedback method", 1);

        feedbackRepository.create(feedback);
        assertThat(feedbackRepository.showAll().size()).isEqualTo(1);

        feedbackRepository.delete(1L);

        assertThat(feedbackRepository.showAll().size()).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void check_to_right_work_update_method() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L, "user",
                "check feedback method", 1);

        feedbackRepository.create(feedback);
        String newFeedback = "check update method";
        feedback.setFeedback(newFeedback);

        feedbackRepository.update(1L, feedback);

        Feedback updatedFeedback = feedbackRepository.getById(1L);

        assertThat(updatedFeedback).extracting(Feedback::getFeedback).isEqualTo(newFeedback);
    }

}
