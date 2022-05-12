package com.geekhub.feedback;

import com.geekhub.exception.FeedbackNotFoundException;
import com.geekhub.exception.ValidationException;
import com.geekhub.movie.Movie;
import com.geekhub.movie.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);
    private final FeedbackRepository feedbackRepository;
    private final MovieService movieService;

    public FeedbackService(FeedbackRepository feedbackRepository, MovieService movieService) {
        this.feedbackRepository = feedbackRepository;
        this.movieService = movieService;
    }

    public List<Feedback> showAllByFilmId(int id) {
        logger.info(String.format("Showed all feedbacks by movie with id - %s", id));
        return feedbackRepository.showAllByFilmId(id);
    }

    public Feedback getFeedback(Long id) {
        Optional<Feedback> feedback = Optional.ofNullable(feedbackRepository.getById(id));
        logger.info("Showed feedback with id - " + id);

        return feedback.orElseThrow(() -> new FeedbackNotFoundException(id));
    }

    public void delete(Long id) {
        if (feedbackRepository.getById(id) == null) {
            logger.error("Feedback was not found");
            throw new FeedbackNotFoundException(id);
        } else {
            logger.info("Was deleted feedback with id - " + id);
            feedbackRepository.delete(id);
        }
    }

    public void create(Feedback feedback) {
        feedback.setTime(LocalDateTime.now());
        if (feedback.getFeedback() == null || feedback.getFeedback().isBlank()) {
            logger.error("Was input empty feedback");
            throw new ValidationException("You can`t leave the empty feedback");
        } else {
            logger.info("Was added new feedback to movie with id - " + feedback.getMovieId());
            feedbackRepository.create(feedback);
            changeMovieRating(feedback.getMovieId());
        }
    }

    public void update(Long id, Feedback updateFeedback) {
        updateFeedback.setTime(LocalDateTime.now());

        if (feedbackRepository.getById(id) == null) {
            logger.error("Feedback was not found");
            throw new FeedbackNotFoundException(id);
        } else if (updateFeedback.getFeedback() == null || updateFeedback.getFeedback().isBlank()) {
            logger.error("Was input empty feedback");
            throw new ValidationException("You can`t leave the empty feedback");
        } else {
            logger.info("Was updated feedback with id - " + id);

            feedbackRepository.update(id, updateFeedback);
            changeMovieRating(updateFeedback.getMovieId());
        }
    }

    private void changeMovieRating(int movieId) {
        List<Feedback> feedbacks = feedbackRepository.showAllByFilmId(movieId);
        List<Integer> scores = feedbacks.stream().map(Feedback::getMovieScore).toList();

        int sum = scores.stream().mapToInt(Integer::intValue).sum();
        int average = sum / scores.size();

        Movie movie = movieService.show(movieId);
        movie.setAverageRating(average);
        movieService.update(movieId, movie);
    }
}
