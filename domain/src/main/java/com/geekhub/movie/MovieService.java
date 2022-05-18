package com.geekhub.movie;

import com.geekhub.event.EventService;
import com.geekhub.exception.DeleteMovieException;
import com.geekhub.exception.MovieNotFoundException;
import com.geekhub.exception.ValidationException;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final EventService eventService;

    public MovieService(MovieRepository movieRepository, EventService eventService) {
        this.movieRepository = movieRepository;
        this.eventService = eventService;
    }

    public void delete(int id) {
        if (movieRepository.show(id) == null) {
            throw new MovieNotFoundException(id);
        } else if (eventService.findEventsByMovieId(id).size() > 0) {
            throw new DeleteMovieException("There are events created with this movie, delete the events first");
        } else {
            logger.info("Was deleted movie with id - " + id);
            movieRepository.delete(id);
        }
    }

    public Movie show(int id) {
        Optional<Movie> movie = Optional.ofNullable(movieRepository.show(id));
        logger.info("Was showed movie with id - " + id);

        return movie.orElseThrow(() -> new MovieNotFoundException(id));
    }

    public Page<Movie> showAll(Pageable pageable) {
        logger.info("Was showed all movies");
        return showMoviesPage(pageable, movieRepository.showAll());
    }

    public void create(Movie movie) {
        checkInputData(movie);
        movieRepository.create(movie);
        logger.info("Was added new movie");
    }

    public void update(int id, Movie updatedMovie) {
        checkInputData(updatedMovie);
        if (movieRepository.show(id) == null) {
            throw new MovieNotFoundException(id);
        }
        movieRepository.update(id, updatedMovie);
        logger.info("Was updated movie with id - " + id);
    }

    public List<Movie> search(String keyWord) {
        if (keyWord == null || keyWord.isBlank()) {
            return Collections.emptyList();
        } else {
            logger.info("Was showed movies by key word - " + keyWord);
            return movieRepository.search(keyWord);
        }
    }

    public List<Movie> showSortedByGenre(String genre) {
        logger.info("Was showed all movies sorted by genre");
        return movieRepository.showSortedByGenre(genre);
    }

    public List<Movie> showSortedByCountry(String production) {
        logger.info("Was showed all movies sorted by release country");
        return movieRepository.showSortedByCountry(production);
    }

    public Page<Movie> showSortedByAscDate(Pageable pageable) {
        logger.info("Was showed all movies sorted by ASC date");
        List<Movie> movies = movieRepository.showAll()
                .stream()
                .sorted(Comparator.comparing(Movie::getRelease)).collect(Collectors.toList());

        return showMoviesPage(pageable, movies);
    }

    public Page<Movie> showSortedByRating(Pageable pageable) {
        logger.info("Was showed all movies sorted by rating");
        return showMoviesPage(pageable, movieRepository.showSortedByRating());
    }

    public List<Movie> showLast3Movies() {
        logger.info("Was showed 3 coming soon movies");
        return movieRepository.showAllLastMovie();
    }

    private Page<Movie> showMoviesPage(Pageable pageable, List<Movie> movies) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Movie> list;
        if (movies.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, movies.size());
            list = movies.subList(startItem, toIndex);
        }

        logger.info("Was showed all movies");
        return new PageImpl<Movie>(list, PageRequest.of(currentPage, pageSize), movies.size());
    }

    private void checkInputData(Movie movie) {
        if (movie.getTitle() == null || movie.getTitle().isBlank()) {
            throw new ValidationException("Was not to inputted title of the movie");
        } else if (movie.getDescription() == null || movie.getDescription().isBlank()) {
            throw new ValidationException("Was not to inputted description of the movie");
        } else if (movie.getActors() == null || movie.getActors().isEmpty()) {
            throw new ValidationException("Was not to inputted actors of the movie");
        } else if (movie.getCountry() == null) {
            throw new ValidationException("Was not to inputted releases country of the movie");
        } else if (movie.getRelease() == null) {
            throw new ValidationException("Was not to inputted date of the release movie");
        } else if (movie.getImage().length < 1) {
            throw new ValidationException("Was not to inputted image of the movie");
        } else if (movie.getTrailer() == null || movie.getTrailer().isBlank() || !checkValidUrl(movie.getTrailer())) {
            throw new ValidationException("Was not to inputted link on the trailer of the movie or input link is invalid");
        }
    }

    private boolean checkValidUrl(String trailerLink) {
        String[] schemes = {"http", "https", "ftp"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        return urlValidator.isValid(trailerLink);
    }
}
