package com.geekhub.movie;

import com.geekhub.exception.MovieNotFoundException;
import com.geekhub.models.Genre;
import com.geekhub.models.Production;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {MovieRepository.class, MovieRowMapper.class})
@Sql(scripts = "classpath:schema.sql")
class MovieRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MovieRowMapper movieRowMapper;

    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE movie");
    }

    @Test
    void no_history_records_in_db() {
        long moviesCount = movieRepository.showAll().size();
        assertThat(moviesCount).isZero();
    }

    @Test
    void nothing_happened_when_trying_to_delete_not_existing_movie() {
        assertThatCode(() -> movieRepository.delete(1))
                .doesNotThrowAnyException();
    }

    @Test
    @DirtiesContext
    void check_add_movie() {
        assertThat(movieRepository.showAll().size()).isEqualTo(0);

        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie);

        assertThat(movieRepository.showAll().size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void check_multiply_add_movie() {
        assertThat(movieRepository.showAll().size()).isEqualTo(0);

        Movie movie1 = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie2 = new Movie(2, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie1);
        movieRepository.create(movie2);

        assertThat(movieRepository.showAll().size()).isEqualTo(2);
    }

    @Test
    void when_movie_was_not_found() {
        assertThrows(MovieNotFoundException.class, () -> movieRepository.show(1));
    }

    @Test
    @DirtiesContext
    void check_return_right_movie() {
        Movie movie = new Movie(9, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie);

        Movie movieFromDb = movieRepository.show(9);

        assertThat(movieFromDb).extracting(Movie::getTitle).isEqualTo(movie.getTitle());
        assertThat(movieFromDb).extracting(Movie::getDescription).isEqualTo(movie.getDescription());
    }

    @Test
    @DirtiesContext
    void check_delete_movie() {
        assertThat(movieRepository.showAll().size()).isEqualTo(0);

        Movie movie = new Movie(9, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie);
        assertThat(movieRepository.showAll().size()).isEqualTo(1);

        movieRepository.delete(9);
        assertThat(movieRepository.showAll().size()).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void check_result_update_method() {
        Movie movie = new Movie(9, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie);
        String newTitle = "Title update movie";
        movie.setTitle(newTitle);

        movieRepository.update(9, movie);
        Movie movieFromDb = movieRepository.show(9);

        assertThat(movieFromDb).extracting(Movie::getTitle).isEqualTo(newTitle);
    }

    @Test
    @DirtiesContext
    void check_search_method_all_cases() {
        String keyword = "tit";
        assertThat(movieRepository.search(keyword).size()).isEqualTo(0);

        Movie movie1 = new Movie(9, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie2 = new Movie(10, "Titanic", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie3 = new Movie(11, "Entity", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie4 = new Movie(12, "Another", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie1);
        movieRepository.create(movie2);
        movieRepository.create(movie3);
        movieRepository.create(movie4);

        assertThat(movieRepository.search(keyword).size()).isEqualTo(3);
    }

    @Test
    @DirtiesContext
    void check_right_work_sort_by_genre() {
        String genre = "Drama";
        assertThat(movieRepository.showSortedByGenre(genre).size()).isEqualTo(0);

        Movie movie1 = new Movie(9, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie2 = new Movie(10, "Titanic", Genre.DRAMA, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie1);
        movieRepository.create(movie2);

        assertThat(movieRepository.showSortedByGenre(genre).size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void check_right_work_sort_by_release_country() {
        String country = "USA";
        assertThat(movieRepository.showSortedByCountry(country).size()).isEqualTo(0);

        Movie movie1 = new Movie(9, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie2 = new Movie(10, "Titanic", Genre.DRAMA, "description", LocalDate.now(),
                Production.UKRAINE, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie1);
        movieRepository.create(movie2);

        assertThat(movieRepository.showSortedByCountry(country).size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void check_return_last_3_movie() {
        assertThat(movieRepository.showAllLastMovie().size()).isEqualTo(0);

        Movie movie1 = new Movie(9, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie2 = new Movie(10, "Titanic", Genre.COMEDY, "description", LocalDate.now().minusDays(1),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie3 = new Movie(11, "Entity", Genre.COMEDY, "description", LocalDate.now().plusDays(1),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);
        Movie movie4 = new Movie(12, "Another", Genre.COMEDY, "description", LocalDate.now().minusMonths(1),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        movieRepository.create(movie1);
        movieRepository.create(movie2);
        movieRepository.create(movie3);
        movieRepository.create(movie4);

        List<Movie> movies = movieRepository.showAllLastMovie();

        assertThat(movieRepository.showAllLastMovie().size()).isEqualTo(3);
        assertThat(movies.get(0)).extracting(Movie::getId).isEqualTo(11);
        assertThat(movies.get(1)).extracting(Movie::getId).isEqualTo(9);
        assertThat(movies.get(2)).extracting(Movie::getId).isEqualTo(10);
    }

    @Test
    @DirtiesContext
    void check_return_sorted_by_rating() {
        assertThat(movieRepository.showSortedByRating().size()).isEqualTo(0);

        Movie movie1 = new Movie(9, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 9, new byte[100]);
        Movie movie2 = new Movie(10, "Titanic", Genre.COMEDY, "description", LocalDate.now().minusDays(1),
                Production.USA, List.of("actors"), "trailer", 8, new byte[100]);
        Movie movie3 = new Movie(11, "Entity", Genre.COMEDY, "description", LocalDate.now().plusDays(1),
                Production.USA, List.of("actors"), "trailer", 10, new byte[100]);

        movieRepository.create(movie1);
        movieRepository.create(movie2);
        movieRepository.create(movie3);

        List<Movie> movies = movieRepository.showAllLastMovie();

        assertThat(movieRepository.showAllLastMovie().size()).isEqualTo(3);
        assertThat(movies.get(0)).extracting(Movie::getId).isEqualTo(11);
        assertThat(movies.get(1)).extracting(Movie::getId).isEqualTo(9);
        assertThat(movies.get(2)).extracting(Movie::getId).isEqualTo(10);
    }

    @Test
    @DirtiesContext
    void check_return_image_bytes() {
        Movie movie = new Movie(9, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 9, new byte[100]);

        movieRepository.create(movie);

        byte[] bytes = movieRepository.getImage(9);

        assertThat(bytes).isEqualTo(movie.getImage());
    }
}
