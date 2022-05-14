package com.geekhub.feedback;

import com.geekhub.exception.FeedbackNotFoundException;
import com.geekhub.exception.ValidationException;
import com.geekhub.movie.Movie;
import com.geekhub.movie.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    FeedbackRepository feedbackRepository;

    @Mock
    MovieService movieService;

    @InjectMocks
    FeedbackService feedbackService;

    @Test
    void showAllByFilmId_check_call_repository_method() {
        List<Feedback> feedbacks = new ArrayList<>();
        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);

        feedbackService.showAllByFilmId(1);

        verify(feedbackRepository).showAllByFilmId(1);
    }

    @Test
    void showAllByFilmId_check_result() {
        List<Feedback> expected = new ArrayList<>();
        when(feedbackRepository.showAllByFilmId(1)).thenReturn(expected);

        List<Feedback> actual = feedbackService.showAllByFilmId(1);

        assertEquals(expected, actual);
    }

    @Test
    void showAllByFilmId_check_result_size() {
        List<Feedback> expected = new ArrayList<>();
        when(feedbackRepository.showAllByFilmId(1)).thenReturn(expected);

        List<Feedback> actual = feedbackService.showAllByFilmId(1);

        assertEquals(expected.size(), actual.size());
    }

    @Test
    void getFeedback_check_call_repository_method() {
        Feedback feedback = new Feedback();
        when(feedbackRepository.getById(1L)).thenReturn(feedback);

        feedbackService.getFeedback(1L);

        verify(feedbackRepository).getById(1L);
    }

    @Test
    void getFeedback_when_return_null() {
        Feedback feedback = null;
        when(feedbackRepository.getById(1L)).thenReturn(feedback);

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.getFeedback(1L));
    }

    @Test
    void getFeedback_check_return_object() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "feedback", 10);
        when(feedbackRepository.getById(1L)).thenReturn(feedback);

        Feedback actual = feedbackService.getFeedback(1L);

        assertEquals(feedback, actual);
    }

    @Test
    void getFeedback_when_success_call_method() {
        Feedback feedback = new Feedback();
        when(feedbackRepository.getById(1L)).thenReturn(feedback);

        assertDoesNotThrow(() -> feedbackService.getFeedback(1L));
    }

    @Test
    void delete_check_call_get_method() {
        Feedback feedback = new Feedback();
        when(feedbackRepository.getById(1L)).thenReturn(feedback);

        feedbackService.delete(1L);

        verify(feedbackRepository).getById(1L);
    }

    @Test
    void delete_if_getMethod_return_null() {
        when(feedbackRepository.getById(1L)).thenReturn(null);

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.delete(1L));
    }

    @Test
    void delete_check_call_repository_method_toDelete() {
        when(feedbackRepository.getById(1L)).thenReturn(new Feedback());

        feedbackService.delete(1L);

        verify(feedbackRepository).delete(1L);
    }

    @Test
    void when_success_delete() {
        when(feedbackRepository.getById(1L)).thenReturn(new Feedback());

        assertDoesNotThrow(() -> feedbackService.delete(1L));
    }

    @Test
    void create_when_feedbackText_is_null() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", null, 10);

        assertThrows(ValidationException.class, () -> feedbackService.create(feedback));
    }

    @Test
    void create_when_feedbackText_is_empty() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "", 10);

        assertThrows(ValidationException.class, () -> feedbackService.create(feedback));
    }

    @Test
    void create_set_feedback_time() {
        Feedback feedback = new Feedback(1L, null, 1, 1L,
                "User", "feedback", 10);
        List<Feedback> feedbacks = List.of(feedback);
        Movie movie = new Movie();

        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);
        when(movieService.show(1)).thenReturn(movie);

        feedbackService.create(feedback);

        assertNotNull(feedback.getTime());
        assertEquals(LocalDateTime.now().toLocalDate(), feedback.getTime().toLocalDate());
    }

    @Test
    void create_check_call_repository_method() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "feedback", 10);
        List<Feedback> feedbacks = List.of(feedback);
        Movie movie = new Movie();

        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);
        when(movieService.show(1)).thenReturn(movie);

        feedbackService.create(feedback);

        verify(feedbackRepository).create(feedback);
    }

    @Test
    void create_check_call_method_changeMovieRating() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "feedback", 10);
        List<Feedback> feedbacks = List.of(feedback);
        Movie movie = new Movie();

        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);
        when(movieService.show(1)).thenReturn(movie);

        feedbackService.create(feedback);

        verify(feedbackRepository).showAllByFilmId(1);
        verify(movieService).show(1);
        verify(movieService).update(1, movie);
    }

    @Test
    void success_create_feedback() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "feedback", 10);
        List<Feedback> feedbacks = List.of(feedback);
        Movie movie = new Movie();

        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);
        when(movieService.show(1)).thenReturn(movie);

        assertDoesNotThrow(() -> feedbackService.create(feedback));
    }

    @Test
    void update_check_set_time() {
        Feedback feedback = new Feedback(1L, null, 1, 1L,
                "User", "feedback", 10);
        List<Feedback> feedbacks = List.of(feedback);
        Movie movie = new Movie();

        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);
        when(movieService.show(1)).thenReturn(movie);
        when(feedbackRepository.getById(1L)).thenReturn(feedback);

        feedbackService.update(1L, feedback);

        assertNotNull(feedback.getTime());
        assertEquals(LocalDateTime.now().toLocalDate(), feedback.getTime().toLocalDate());
    }

    @Test
    void update_when_feedbackToUpdate_is_not_found() {
        when(feedbackRepository.getById(1L)).thenReturn(null);

        assertThrows(FeedbackNotFoundException.class, () -> feedbackService.update(1L, new Feedback()));
    }

    @Test
    void update_when_feedbackText_is_null() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", null, 10);
        when(feedbackRepository.getById(1L)).thenReturn(feedback);

        assertThrows(ValidationException.class, () -> feedbackService.update(1L, feedback));
    }

    @Test
    void update_when_feedbackText_is_empty() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "", 10);
        when(feedbackRepository.getById(1L)).thenReturn(feedback);

        assertThrows(ValidationException.class, () -> feedbackService.update(1L, feedback));
    }

    @Test
    void update_check_call_repository_method() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "feedback", 10);
        List<Feedback> feedbacks = List.of(feedback);
        Movie movie = new Movie();

        when(feedbackRepository.getById(1L)).thenReturn(feedback);
        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);
        when(movieService.show(1)).thenReturn(movie);

        feedbackService.update(1L, feedback);

        verify(feedbackRepository).update(1L, feedback);
    }

    @Test
    void update_check_call_method_changeMovieRating() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "feedback", 10);
        List<Feedback> feedbacks = List.of(feedback);
        Movie movie = new Movie();

        when(feedbackRepository.getById(1L)).thenReturn(feedback);
        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);
        when(movieService.show(1)).thenReturn(movie);

        feedbackService.update(1L, feedback);

        verify(feedbackRepository).showAllByFilmId(1);
        verify(movieService).show(1);
        verify(movieService).update(1, movie);
    }

    @Test
    void success_update_feedback() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L,
                "User", "feedback", 10);
        List<Feedback> feedbacks = List.of(feedback);
        Movie movie = new Movie();

        when(feedbackRepository.getById(1L)).thenReturn(feedback);
        when(feedbackRepository.showAllByFilmId(1)).thenReturn(feedbacks);
        when(movieService.show(1)).thenReturn(movie);

        assertDoesNotThrow(() -> feedbackService.update(1L, feedback));
    }

}
