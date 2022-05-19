package com.geekhub.movie;

import com.geekhub.event.Event;
import com.geekhub.event.EventService;
import com.geekhub.exception.DeleteMovieException;
import com.geekhub.exception.MovieNotFoundException;
import com.geekhub.exception.ValidationException;
import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Autowired
    Pageable pageable;

    @Mock
    MovieRepository movieRepository;

    @Mock
    EventService eventService;

    @InjectMocks
    MovieService movieService;

    @Test
    void delete_check_call_method() {
        Movie movie = new Movie();
        when(movieRepository.show(1)).thenReturn(movie);

        movieService.delete(1);

        verify(movieRepository).show(1);
    }

    @Test
    void delete_if_movieToDelete_not_found() {
        when(movieRepository.show(1)).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> movieService.delete(1));
    }

    @Test
    void delete_when_created_events_with_this_movie() {
        Movie movie = new Movie();
        Event event = new Event();
        List<Event> events = List.of(event);

        when(movieRepository.show(1)).thenReturn(movie);
        when(eventService.findEventsByMovieId(1)).thenReturn(events);

        assertThrows(DeleteMovieException.class, () -> movieService.delete(1));
    }

    @Test
    void check_call_repository_method_to_delete() {
        Movie movie = new Movie();

        when(movieRepository.show(1)).thenReturn(movie);
        when(eventService.findEventsByMovieId(1)).thenReturn(Collections.emptyList());

        movieService.delete(1);

        verify(movieRepository).delete(1);
    }

    @Test
    void success_delete_movie() {
        Movie movie = new Movie();

        when(movieRepository.show(1)).thenReturn(movie);
        when(eventService.findEventsByMovieId(1)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> movieService.delete(1));
    }

    @Test
    void showMovie_check_call_repository_method() {
        Movie movie = new Movie();
        when(movieRepository.show(1)).thenReturn(movie);

        movieService.show(1);

        verify(movieRepository).show(1);
    }

    @Test
    void showMovie_when_movie_not_found() {
        when(movieRepository.show(1)).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> movieService.show(1));
    }

    @Test
    void check_return_right_movie() {
        Movie movie = new Movie();
        when(movieRepository.show(1)).thenReturn(movie);

        Movie actual = movieService.show(1);

        assertEquals(movie, actual);
    }

    @Test
    void showMovie_success_result() {
        Movie movie = new Movie();
        when(movieRepository.show(1)).thenReturn(movie);

        assertDoesNotThrow(() -> movieService.show(1));
    }

    @Test
    void showAll_check_call_repository_method() {
        List<Movie> movies = new ArrayList<>();
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showAll()).thenReturn(movies);

        movieService.showAll(pageable);

        verify(movieRepository).showAll();
    }

    @Test
    void showAll_right_movies_page_size() {
        List<Movie> movies = List.of(new Movie(), new Movie(), new Movie(), new Movie());
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showAll()).thenReturn(movies);

        Page<Movie> moviePage = movieService.showAll(pageable);

        assertEquals(3, moviePage.getSize());
    }

    @Test
    void showAll_right_movies_page() {
        List<Movie> movies = List.of(new Movie(), new Movie(), new Movie());
        Page<Movie> expected = new PageImpl<Movie>(movies, PageRequest.of(0, 3), movies.size());
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showAll()).thenReturn(movies);

        Page<Movie> moviePage = movieService.showAll(pageable);

        assertEquals(expected, moviePage);
    }

    @Test
    void search_if_keyword_is_null() {
        String keyword = null;

        List<Movie> movies = movieService.search(keyword);

        assertEquals(0, movies.size());
    }

    @Test
    void search_if_keyword_is_empty() {
        String keyword = "";

        List<Movie> movies = movieService.search(keyword);

        assertEquals(0, movies.size());
    }

    @Test
    void search_check_call_repository() {
        List<Movie> movies = new ArrayList<>();
        String keyword = "keyword";

        when(movieRepository.search(keyword)).thenReturn(movies);

        movieService.search(keyword);

        verify(movieRepository).search(keyword);
    }

    @Test
    void search_check_return_result() {
        List<Movie> expected = new ArrayList<>();
        String keyword = "keyword";

        when(movieRepository.search(keyword)).thenReturn(expected);

        List<Movie> actual = movieService.search(keyword);

        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void sortByGenre_check_call_repository() {
        List<Movie> movies = new ArrayList<>();
        String genre = "genre";

        when(movieRepository.showSortedByGenre(genre)).thenReturn(movies);

        movieService.showSortedByGenre(genre);

        verify(movieRepository).showSortedByGenre(genre);
    }

    @Test
    void sortByGenre_check_return() {
        List<Movie> movies = List.of(new Movie());
        String genre = "genre";

        when(movieRepository.showSortedByGenre(genre)).thenReturn(movies);

        List<Movie> actual = movieService.showSortedByGenre(genre);

        assertEquals(movies, actual);
        assertEquals(movies.size(), actual.size());
    }

    @Test
    void sortByCountry_check_call_repository() {
        List<Movie> movies = new ArrayList<>();
        String country = "country";

        when(movieRepository.showSortedByCountry(country)).thenReturn(movies);

        movieService.showSortedByCountry(country);

        verify(movieRepository).showSortedByCountry(country);
    }

    @Test
    void sortByCountry_check_return() {
        List<Movie> movies = List.of(new Movie());
        String country = "country";

        when(movieRepository.showSortedByCountry(country)).thenReturn(movies);

        List<Movie> actual = movieService.showSortedByCountry(country);

        assertEquals(movies, actual);
        assertEquals(movies.size(), actual.size());
    }

    @Test
    void showLast3Movies_check_call_repository() {
        List<Movie> movies = List.of(new Movie());
        when(movieRepository.showAllLastMovie()).thenReturn(movies);

        movieService.showLast3Movies();

        verify(movieRepository).showAllLastMovie();
    }

    @Test
    void showLast3Movies_check_return_result() {
        List<Movie> expected = List.of(new Movie(), new Movie(), new Movie());

        when(movieRepository.showAllLastMovie()).thenReturn(expected);

        List<Movie> actual = movieService.showLast3Movies();

        assertEquals(expected, actual);
    }

    @Test
    void showLast3Movies_check_return_result_size() {
        List<Movie> expected = List.of(new Movie(), new Movie(), new Movie());

        when(movieRepository.showAllLastMovie()).thenReturn(expected);

        List<Movie> actual = movieService.showLast3Movies();

        assertEquals(expected.size(), actual.size());
    }

    @Test
    void sortedByRating_check_call_repository_method() {
        List<Movie> movies = new ArrayList<>();
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showSortedByRating()).thenReturn(movies);

        movieService.showSortedByRating(pageable);

        verify(movieRepository).showSortedByRating();
    }

    @Test
    void sortedByRating_right_movies_page_size() {
        List<Movie> movies = List.of(new Movie(), new Movie(), new Movie(), new Movie());
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showSortedByRating()).thenReturn(movies);

        Page<Movie> moviePage = movieService.showSortedByRating(pageable);

        assertEquals(3, moviePage.getSize());
    }

    @Test
    void sortedByRating_right_movies_page() {
        List<Movie> movies = List.of(new Movie(), new Movie(), new Movie());
        Page<Movie> expected = new PageImpl<Movie>(movies, PageRequest.of(0, 3), movies.size());
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showSortedByRating()).thenReturn(movies);

        Page<Movie> moviePage = movieService.showSortedByRating(pageable);

        assertEquals(expected, moviePage);
    }

    @Test
    void sortedByAsc_check_repository_method_call() {
        List<Movie> movies = new ArrayList<>();
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showAll()).thenReturn(movies);

        movieService.showSortedByAscDate(pageable);

        verify(movieRepository).showAll();
    }

    @Test
    void sortedByAsc_right_movies_page_size() {
        Movie movie1 = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11), Production.USA, List.of("actors"), "path", "http", 7);
        Movie movie2 = new Movie(2, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 11, 11), Production.USA, List.of("actors"), "path", "http", 7);
        Movie movie3 = new Movie(3, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 10, 11), Production.USA, List.of("actors"), "path", "http", 7);
        Movie movie4 = new Movie(4, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 9), Production.USA, List.of("actors"), "path", "http", 7);
        List<Movie> movies = List.of(movie1, movie2, movie3, movie4);
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showAll()).thenReturn(movies);

        Page<Movie> moviePage = movieService.showSortedByAscDate(pageable);

        assertEquals(3, moviePage.getSize());
    }

    @Test
    void sortedByAsc_right_movies_page() {
        Movie movie1 = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11), Production.USA, List.of("actors"), "path", "http", 7);
        Movie movie2 = new Movie(2, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 11, 11), Production.USA, List.of("actors"), "path", "http", 7);
        Movie movie3 = new Movie(3, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 10, 11), Production.USA, List.of("actors"), "path", "http", 7);
        Movie movie4 = new Movie(4, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 9), Production.USA, List.of("actors"), "path", "http", 7);
        List<Movie> movies = List.of(movie1, movie2, movie3, movie4);
        List<Movie> sortList = List.of(movie3, movie2, movie4);
        Page<Movie> expected = new PageImpl<Movie>(sortList, PageRequest.of(0, 3), movies.size());
        pageable = PageRequest.of(0, 3);

        when(movieRepository.showAll()).thenReturn(movies);

        Page<Movie> moviePage = movieService.showSortedByAscDate(pageable);

        assertEquals(expected, moviePage);
    }

    @Test
    void create_when_title_is_null() {
        Movie movie = new Movie(1, null, Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_title_is_empty() {
        Movie movie = new Movie(1, "", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_description_is_empty() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_description_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, null, LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_list_actors_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, null, "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_list_actors_is_empty() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, Collections.emptyList(), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_release_country_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                null, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_release_date_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", null,
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_image_array_is_empty() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "http", 7, new byte[0]);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_trailer_name_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), null, 7, new byte[100]);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_trailer_name_is_empty() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "", 7, new byte[100]);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_when_trailer_link_is_not_valid() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "not valid", 7, new byte[100]);

        assertThrows(ValidationException.class, () -> movieService.create(movie));
    }

    @Test
    void create_check_call_repository_method() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "https://www.youtube.com/watch?v=kVrqfYjkTdQ&t=10s", 7, new byte[100]);

        movieService.create(movie);

        verify(movieRepository).create(movie);
    }

    @Test
    void success_create_movie() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "https://www.youtube.com/watch?v=kVrqfYjkTdQ&t=10s", 7, new byte[100]);

        assertDoesNotThrow(() -> movieService.create(movie));
    }

    @Test
    void update_when_title_is_null() {
        Movie movie = new Movie(1, null, Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_title_is_empty() {
        Movie movie = new Movie(1, "", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_description_is_empty() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_description_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, null, LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_list_actors_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, null, "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_list_actors_is_empty() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, Collections.emptyList(), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_release_country_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                null, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_release_date_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", null,
                Production.USA, List.of("actors"), "path", "http", 7);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_image_array_is_empty() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "http", 7, new byte[0]);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_trailer_name_is_null() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), null, 7, new byte[100]);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_trailer_name_is_empty() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "", 7, new byte[100]);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_trailer_link_is_not_valid() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "not valid", 7, new byte[100]);

        assertThrows(ValidationException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_when_movie_not_found() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "https://www.youtube.com/watch?v=kVrqfYjkTdQ&t=10s", 7, new byte[100]);

        when(movieRepository.show(1)).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> movieService.update(1, movie));
    }

    @Test
    void update_check_call_repository_method() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "https://www.youtube.com/watch?v=kVrqfYjkTdQ&t=10s", 7, new byte[100]);

        when(movieRepository.show(1)).thenReturn(movie);

        movieService.update(1, movie);

        verify(movieRepository).update(1, movie);
    }

    @Test
    void success_update_movie() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "https://www.youtube.com/watch?v=kVrqfYjkTdQ&t=10s", 7, new byte[100]);

        when(movieRepository.show(1)).thenReturn(movie);

        assertDoesNotThrow(() -> movieService.update(1, movie));
    }

    @Test
    void getImage_call_checked_method() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "https://www.youtube.com/watch?v=kVrqfYjkTdQ&t=10s", 7, new byte[100]);

        when(movieRepository.show(1)).thenReturn(movie);

        movieService.getImageBytes(1);

        verify(movieRepository).show(1);
    }

    @Test
    void getImage_when_movie_not_found() {
        when(movieRepository.show(1)).thenReturn(null);

        assertThrows(MovieNotFoundException.class, () -> movieService.getImageBytes(1));
    }

    @Test
    void getImage_check_repository_method() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "https://www.youtube.com/watch?v=kVrqfYjkTdQ&t=10s", 7, new byte[100]);

        when(movieRepository.show(1)).thenReturn(movie);

        movieService.getImageBytes(1);

        verify(movieRepository).getImage(1);
    }

    @Test
    void getImage_success_get_bytes() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "desc", LocalDate.of(2021, 12, 11),
                Production.USA, List.of("actors"), "https://www.youtube.com/watch?v=kVrqfYjkTdQ&t=10s", 7, new byte[100]);

        when(movieRepository.show(1)).thenReturn(movie);

        assertDoesNotThrow(() -> movieService.getImageBytes(1));
    }
}
