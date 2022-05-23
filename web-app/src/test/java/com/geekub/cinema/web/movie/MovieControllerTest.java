package com.geekub.cinema.web.movie;

import com.geekhub.exception.MovieNotFoundException;
import com.geekhub.feedback.FeedbackConverter;
import com.geekhub.feedback.FeedbackService;
import com.geekhub.models.Gender;
import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import com.geekhub.models.Role;
import com.geekhub.movie.Movie;
import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import com.geekhub.movie.dto.MovieCreateDto;
import com.geekhub.movie.dto.MovieDto;
import com.geekhub.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(scripts = "classpath:schema.sql")
@WithMockUser(username = "admin", roles = "ADMIN")
class MovieControllerTest {

    @Autowired
    MovieController movieController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    MovieService movieService;

    @Autowired
    @MockBean
    FeedbackService feedbackService;

    @Autowired
    @MockBean
    FeedbackConverter feedbackConverter;

    @Autowired
    @MockBean
    MovieConverter movieConverter;

    @Test
    void getAllMoviesFirstPage_check_request() throws Exception {
        when(movieService.showAll(PageRequest.of(0, 3))).thenReturn(Page.empty());

        mockMvc.perform(get("/movies").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk());
    }

    @Test
    void showMovies_check_request() throws Exception {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        MovieDto movieDto = new MovieDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        List<Movie> movies = List.of(movie);
        List<MovieDto> moviesDto = List.of(movieDto);
        Page<Movie> pageMovies = new PageImpl<Movie>(movies, PageRequest.of(1, 3), 1);

        when(movieService.showAll(PageRequest.of(0, 3))).thenReturn(pageMovies);
        when(movieConverter.convertToListDto(pageMovies.getContent())).thenReturn(moviesDto);

        mockMvc.perform(get("/movies/page/1").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/movies-menu"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", pageMovies.getTotalPages()))
                .andExpect(model().attribute("totalItems", pageMovies.getTotalElements()))
                .andExpect(model().attribute("movies", moviesDto))
                .andExpect(model().attribute("checkUserRights", false));
    }

    @Test
    void showMovies_if_current_page_more_than_total_pages() throws Exception {
        when(movieService.showAll(PageRequest.of(5, 3))).thenThrow(MovieNotFoundException.class);

        mockMvc.perform(get("/movies/page/5").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(view().name("error"));
    }

    @Test
    void showMovie_check_request() throws Exception {
        MovieDto movieDto = new MovieDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);

        when(feedbackConverter.convertToListDto(feedbackService.showAllByFilmId(1))).thenReturn(Collections.emptyList());
        when(movieConverter.convertToMovieDto(movieService.show(1))).thenReturn(movieDto);

        mockMvc.perform(get("/movies/1").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/show-movie-details"))
                .andExpect(model().attribute("movie", movieDto))
                .andExpect(model().attribute("feedbacks", Collections.emptyList()))
                .andExpect(model().attribute("checkUserRights", false));
    }

    @Test
    void show_movie_if_wrong_url() throws Exception {
        mockMvc.perform(get("/movies/url"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void if_movie_not_found() throws Exception {
        when(feedbackConverter.convertToListDto(feedbackService.showAllByFilmId(10))).thenReturn(Collections.emptyList());
        when(movieConverter.convertToMovieDto(movieService.show(10))).thenThrow(MovieNotFoundException.class);

        mockMvc.perform(get("/movies/10"))
                .andExpect(view().name("error"));
    }

    @Test
    void deleteMovie_check_request() throws Exception {
        mockMvc.perform(post("/movies/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies"));

        verify(movieService).delete(1);
    }

    @Test
    void editMovie_when_wrong_url() throws Exception {
        mockMvc.perform(get("/movies/url/edit"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editMovie_check_request() throws Exception {
        MovieCreateDto movieDto = new MovieCreateDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        when(movieConverter.convertToMovieCreateDto(movieService.show(1))).thenReturn(movieDto);
        mockMvc.perform(get("/movies/1/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/edit"))
                .andExpect(model().attribute("movie", movieDto));

        verify(movieConverter).convertToMovieCreateDto(movieService.show(1));
    }

    @Test
    void updateMovie_check_request() throws Exception {
        MovieCreateDto movieDto = new MovieCreateDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        when(movieConverter.convertToMovieCreateDto(movieService.show(1))).thenReturn(movieDto);
        when(movieConverter.convertFromDto(movieDto)).thenReturn(movie);

        mockMvc.perform(multipart("/movies/1/update").file("image", new byte[100])
                        .param("title", "title")
                        .param("description", "description")
                        .param("release", String.valueOf(LocalDate.now()))
                        .param("country", String.valueOf(Production.USA))
                        .param("genre", String.valueOf(Genre.COMEDY))
                        .param("actors", "actors")
                        .param("trailer", "trailer")
                        .param("image", "image"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies/1"));

        verify(movieService).update(1, movie);
    }

    @Test
    void createMovie_check_view() throws Exception {
        mockMvc.perform(get("/movies/create-movie"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/create"))
                .andExpect(model().attribute("movie", new MovieCreateDto()));
    }

    @Test
    void addMovie() throws Exception {
        mockMvc.perform(multipart("/movies/create-movie/add").file("image", new byte[100])
                        .param("title", "title")
                        .param("description", "description")
                        .param("release", String.valueOf(LocalDate.now()))
                        .param("country", String.valueOf(Production.USA))
                        .param("genre", String.valueOf(Genre.COMEDY))
                        .param("actors", "actors")
                        .param("trailer", "trailer")
                        .param("image", "image"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies"));

        verify(movieService).create(movieConverter.convertFromDto(new MovieCreateDto()));
    }

    @Test
    void showMoviesSortedByGenre_check_request() throws Exception {
        String genre = "genre";
        MovieDto movieDto = new MovieDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        List<MovieDto> moviesDto = List.of(movieDto);

        when(movieConverter.convertToListDto(movieService.showSortedByGenre(genre))).thenReturn(moviesDto);

        mockMvc.perform(get("/movies/sorted-by-genre")
                        .param("genre", genre)
                        .with(user(new User(1L, "login", "password", Role.USER, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/sorted-by-parameters"))
                .andExpect(model().attribute("movies", moviesDto))
                .andExpect(model().attribute("checkUserRights", false));

        verify(movieConverter).convertToListDto(movieService.showSortedByGenre(genre));
    }

    @Test
    void showMoviesSortedByCountry_check_request() throws Exception {
        String country = "country";
        MovieDto movieDto = new MovieDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        List<MovieDto> moviesDto = List.of(movieDto);

        when(movieConverter.convertToListDto(movieService.showSortedByCountry(country))).thenReturn(moviesDto);

        mockMvc.perform(get("/movies/sorted-by-country")
                        .param("country", country)
                        .with(user(new User(1L, "login", "password", Role.USER, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/sorted-by-parameters"))
                .andExpect(model().attribute("movies", moviesDto))
                .andExpect(model().attribute("checkUserRights", false));

        verify(movieConverter).convertToListDto(movieService.showSortedByCountry(country));
    }

    @Test
    void showMoviesSortedByAscDateFirstPage_check_request() throws Exception {
        when(movieService.showSortedByAscDate(PageRequest.of(0, 3))).thenReturn(Page.empty());

        mockMvc.perform(get("/movies/sorted-by-ASC-date").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk());
    }

    @Test
    void showAscMovies_if_current_page_more_than_total_pages() throws Exception {
        when(movieService.showSortedByAscDate(PageRequest.of(5, 3))).thenThrow(MovieNotFoundException.class);

        mockMvc.perform(get("/movies/sorted-by-ASC-date/5").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(view().name("error"));
    }

    @Test
    void showAscMovies_check_request() throws Exception {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        MovieDto movieDto = new MovieDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        List<Movie> movies = List.of(movie);
        List<MovieDto> moviesDto = List.of(movieDto);
        Page<Movie> pageMovies = new PageImpl<Movie>(movies, PageRequest.of(1, 3), 1);

        when(movieService.showSortedByAscDate(PageRequest.of(0, 3))).thenReturn(pageMovies);
        when(movieConverter.convertToListDto(pageMovies.getContent())).thenReturn(moviesDto);

        mockMvc.perform(get("/movies/sorted-by-ASC-date/1").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/sorted-by-asc"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", pageMovies.getTotalPages()))
                .andExpect(model().attribute("totalItems", pageMovies.getTotalElements()))
                .andExpect(model().attribute("movies", moviesDto))
                .andExpect(model().attribute("checkUserRights", false));
    }

    @Test
    void showMoviesSortedByRatingFirstPage_check_request() throws Exception {
        when(movieService.showSortedByRating(PageRequest.of(0, 3))).thenReturn(Page.empty());

        mockMvc.perform(get("/movies/sorted-by-rating").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk());
    }

    @Test
    void showMoviesSortedByRating_if_current_page_more_than_total_pages() throws Exception {
        when(movieService.showSortedByRating(PageRequest.of(5, 3))).thenThrow(MovieNotFoundException.class);

        mockMvc.perform(get("/movies/sorted-by-rating/5").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(view().name("error"));
    }

    @Test
    void showSortedByRatingMovies_check_request() throws Exception {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        MovieDto movieDto = new MovieDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        List<Movie> movies = List.of(movie);
        List<MovieDto> moviesDto = List.of(movieDto);
        Page<Movie> pageMovies = new PageImpl<Movie>(movies, PageRequest.of(1, 3), 1);

        when(movieService.showSortedByRating(PageRequest.of(0, 3))).thenReturn(pageMovies);
        when(movieConverter.convertToListDto(pageMovies.getContent())).thenReturn(moviesDto);

        mockMvc.perform(get("/movies/sorted-by-rating/1").with(user(new User(1L, "login", "password", Role.USER, "first name",
                        "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/sorted-by-rating"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", pageMovies.getTotalPages()))
                .andExpect(model().attribute("totalItems", pageMovies.getTotalElements()))
                .andExpect(model().attribute("movies", moviesDto))
                .andExpect(model().attribute("checkUserRights", false));
    }

    @Test
    void showMoviesByKeyword() throws Exception {
        String keyword = "keyword";
        MovieDto movieDto = new MovieDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        List<MovieDto> moviesDto = List.of(movieDto);

        when(movieConverter.convertToListDto(movieService.search(keyword))).thenReturn(moviesDto);

        mockMvc.perform(get("/movies/search")
                        .param("keyword", keyword)
                        .with(user(new User(1L, "login", "password", Role.USER, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("movies/sorted-by-parameters"))
                .andExpect(model().attribute("movies", moviesDto))
                .andExpect(model().attribute("checkUserRights", false));

        verify(movieConverter).convertToListDto(movieService.search(keyword));
    }
}
