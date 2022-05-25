package com.geekub.cinema.web.movie;


import com.geekhub.exception.MovieNotFoundException;
import com.geekhub.feedback.FeedbackConverter;
import com.geekhub.feedback.FeedbackService;
import com.geekhub.feedback.dto.FeedbackCreationDto;
import com.geekhub.feedback.dto.FeedbackDto;
import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import com.geekhub.models.Role;
import com.geekhub.movie.Movie;
import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import com.geekhub.movie.dto.MovieCreateDto;
import com.geekhub.movie.dto.MovieDto;
import com.geekhub.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/movies")
public class MovieController {
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;
    private final FeedbackService feedbackService;
    private final FeedbackConverter feedbackConverter;
    private final MovieConverter movieConverter;

    public MovieController(MovieService movieService, FeedbackService feedbackService, FeedbackConverter feedbackConverter, MovieConverter movieConverter) {
        this.movieService = movieService;
        this.feedbackService = feedbackService;
        this.feedbackConverter = feedbackConverter;
        this.movieConverter = movieConverter;
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public String getAllMoviesFirstPage(Model model, @AuthenticationPrincipal User user) {
        return showMovies(1, user, model);
    }

    @GetMapping("/page/{pageNumber}")
    @PreAuthorize("hasRole('USER')")
    public String showMovies(@PathVariable("pageNumber") int currentPage,
                             @AuthenticationPrincipal User user, Model model) {
        Page<Movie> moviePages = movieService.showAll(PageRequest.of(currentPage - 1, 3));
        showMoviesPages(model, user, moviePages, currentPage);

        return "movies/movies-menu";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public String showMovie(@AuthenticationPrincipal User user,
                            @PathVariable("id") int id,
                            @ModelAttribute("newFeedback") FeedbackCreationDto feedback,
                            Model model) {
        List<FeedbackDto> feedbacks =
                feedbackConverter.convertToListDto(feedbackService.showAllByFilmId(id));

        model.addAttribute("movie", movieConverter.convertToMovieDto(movieService.show(id)));
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("checkUserRights", user.getRole().equals(Role.ADMIN));

        return "movies/show-movie-details";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteMovie(@PathVariable("id") int id) {
        movieService.delete(id);

        return "redirect:/movies";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editMovie(Model model, @PathVariable("id") int id) {
        model.addAttribute("movie", movieConverter.convertToMovieCreateDto(movieService.show(id)));
        logger.info("Started operation of edit movie with id - " + id);

        return "movies/edit";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateMovie(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("release") String release,
                              @RequestParam("country") String country,
                              @RequestParam("genre") String genre,
                              @RequestParam("actors") String actors,
                              @RequestParam("trailer") String trailerLink,
                              @RequestParam("image") MultipartFile multipartFile,
                              @PathVariable("id") int id) {

        MovieCreateDto movie = movieConverter.convertToMovieCreateDto(movieService.show(id));
        setMovieParams(title, description, release, country, genre, actors, trailerLink, multipartFile, movie);

        movieService.update(id, movieConverter.convertFromDto(movie));

        return "redirect:/movies/" + id;
    }

    @GetMapping("/create-movie")
    @PreAuthorize("hasRole('ADMIN')")
    public String createMovie(@ModelAttribute("movie") MovieCreateDto movie) {
        logger.info("Started operation of add new movie");
        return "movies/create";
    }

    @PostMapping("/create-movie/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addMovie(@RequestParam("title") String title,
                           @RequestParam("description") String description,
                           @RequestParam("release") String release,
                           @RequestParam("country") String country,
                           @RequestParam("genre") String genre,
                           @RequestParam("actors") String actors,
                           @RequestParam("trailer") String trailerLink,
                           @RequestParam("image") MultipartFile multipartFile) {

        MovieCreateDto movieDto = new MovieCreateDto();
        setMovieParams(title, description, release, country, genre, actors, trailerLink, multipartFile, movieDto);

        movieService.create(movieConverter.convertFromDto(movieDto));

        return "redirect:/movies";
    }

    @GetMapping("/sorted-by-genre")
    @PreAuthorize("hasRole('USER')")
    public String showMoviesSortedByGenre(@AuthenticationPrincipal User user,
                                          @RequestParam("genre") String genre,
                                          Model model) {
        List<MovieDto> moviesDto = movieConverter.convertToListDto(movieService.showSortedByGenre(genre));
        model.addAttribute("movies", moviesDto);
        model.addAttribute("checkUserRights", user.getRole().equals(Role.ADMIN));

        return "movies/sorted-by-parameters";
    }

    @GetMapping("/sorted-by-country")
    @PreAuthorize("hasRole('USER')")
    public String showMoviesSortedByCountry(@AuthenticationPrincipal User user,
                                            @RequestParam("country") String country,
                                            Model model) {
        List<MovieDto> moviesDto = movieConverter.convertToListDto(movieService.showSortedByCountry(country));
        model.addAttribute("movies", moviesDto);
        model.addAttribute("checkUserRights", user.getRole().equals(Role.ADMIN));

        return "movies/sorted-by-parameters";
    }

    @GetMapping("/sorted-by-ASC-date")
    @PreAuthorize("hasRole('USER')")
    public String showMoviesSortedByAscDateFirstPage(Model model, @AuthenticationPrincipal User user) {
        return showAscMovies(1, user, model);
    }

    @GetMapping("/sorted-by-ASC-date/{pageNumber}")
    @PreAuthorize("hasRole('USER')")
    public String showAscMovies(@PathVariable("pageNumber") int currentPage,
                                @AuthenticationPrincipal User user, Model model) {
        Page<Movie> moviePages = movieService.showSortedByAscDate(PageRequest.of(currentPage - 1, 3));
        showMoviesPages(model, user, moviePages, currentPage);

        return "movies/sorted-by-asc";
    }


    @GetMapping("/sorted-by-rating")
    @PreAuthorize("hasRole('USER')")
    public String showMoviesSortedByRatingFirstPage(@AuthenticationPrincipal User user, Model model) {
        return showSortedByRatingMovies(1, user, model);
    }

    @GetMapping("/sorted-by-rating/{pageNumber}")
    @PreAuthorize("hasRole('USER')")
    public String showSortedByRatingMovies(@PathVariable("pageNumber") int currentPage,
                                           @AuthenticationPrincipal User user, Model model) {
        Page<Movie> moviePages = movieService.showSortedByRating(PageRequest.of(currentPage - 1, 3));
        showMoviesPages(model, user, moviePages, currentPage);

        return "movies/sorted-by-rating";
    }


    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public String showMoviesByKeyword(@AuthenticationPrincipal User user,
                                      @RequestParam("keyword") String keyword,
                                      Model model) {
        List<MovieDto> moviesDto = movieConverter.convertToListDto(movieService.search(keyword));
        model.addAttribute("movies", moviesDto);
        model.addAttribute("checkUserRights", user.getRole().equals(Role.ADMIN));

        return "movies/sorted-by-parameters";
    }

    private void setMovieParams(@RequestParam("title") String title,
                                @RequestParam("description") String description,
                                @RequestParam("release") String release,
                                @RequestParam("country") String country,
                                @RequestParam("genre") String genre,
                                @RequestParam("actors") String actors,
                                @RequestParam("trailer") String trailerLink,
                                @RequestParam("image") MultipartFile multipartFile,
                                MovieCreateDto movie) {
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setGenre(Genre.valueOf(genre.toUpperCase(Locale.ROOT)));
        movie.setCountry(Production.valueOf(country.toUpperCase(Locale.ROOT)));
        movie.setRelease(LocalDate.parse(release));
        movie.setActors(List.of(actors));

        if (!multipartFile.isEmpty()) {
            byte[] uploadImageBytes;
            try {
                uploadImageBytes = multipartFile.getBytes();
                movie.setImage(uploadImageBytes);
            } catch (IOException e) {
                logger.error("Convert image to byte was failed");
            }
        }
        if (trailerLink != null || !trailerLink.isBlank()) {
            movie.setTrailer(trailerLink);
        }
    }

    private void showMoviesPages(Model model, @AuthenticationPrincipal User user,
                                 Page<Movie> moviePages, int currentPage) {
        int totalPages = moviePages.getTotalPages();
        long totalItems = moviePages.getTotalElements();
        List<MovieDto> movies = movieConverter.convertToListDto(moviePages.getContent());

        if (currentPage > totalPages || currentPage <= 0) {
            throw new MovieNotFoundException("Page index must not be more than total pages or less than 0!");
        }

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("movies", movies);
        model.addAttribute("checkUserRights", user.getRole().equals(Role.ADMIN));
    }
}
