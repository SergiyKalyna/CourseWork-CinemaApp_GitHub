package com.geekhub.movie;

import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import com.geekhub.movie.dto.MovieCreateDto;
import com.geekhub.movie.dto.MovieDto;
import com.geekhub.movie.dto.MovieDtoForEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MovieConverterTest {

    @InjectMocks
    MovieConverter movieConverter;

    @Test
    void convertFromDto_check_result() {
        MovieCreateDto movieCreateDto = new MovieCreateDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        Movie movie = movieConverter.convertFromDto(movieCreateDto);

        assertThat(movie).extracting(Movie::getId).isEqualTo(movieCreateDto.getId());
        assertThat(movie).extracting(Movie::getTitle).isEqualTo(movieCreateDto.getTitle());
        assertThat(movie).extracting(Movie::getDescription).isEqualTo(movieCreateDto.getDescription());
    }

    @Test
    void convertToMovieDto_check_right_convert() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);

        MovieDto movieDto = movieConverter.convertToMovieDto(movie);

        assertThat(movieDto).extracting(MovieDto::getId).isEqualTo(movie.getId());
        assertThat(movieDto).extracting(MovieDto::getTitle).isEqualTo(movie.getTitle());
        assertThat(movieDto).extracting(MovieDto::getDescription).isEqualTo(movie.getDescription());
    }

    @Test
    void covertToMovieCreateDto_check_result() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);

        MovieCreateDto movieDto = movieConverter.convertToMovieCreateDto(movie);

        assertThat(movieDto).extracting(MovieCreateDto::getId).isEqualTo(movie.getId());
        assertThat(movieDto).extracting(MovieCreateDto::getTitle).isEqualTo(movie.getTitle());
        assertThat(movieDto).extracting(MovieCreateDto::getDescription).isEqualTo(movie.getDescription());
    }

    @Test
    void convertToDto_check_right_convert() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);

        MovieDtoForEvent movieDto = movieConverter.convertToDto(movie);

        assertThat(movieDto).extracting(MovieDtoForEvent::getId).isEqualTo(movie.getId());
        assertThat(movieDto).extracting(MovieDtoForEvent::getTitle).isEqualTo(movie.getTitle());
    }

    @Test
    void check_right_convert_list_to_dto() {
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);
        List<Movie> movies = List.of(movie);

        List<MovieDto> moviesDto = movieConverter.convertToListDto(movies);

        assertNotNull(moviesDto);
        assertEquals(1, moviesDto.size());
        assertThat(moviesDto.get(0)).extracting(MovieDto::getId).isEqualTo(movie.getId());
        assertThat(moviesDto.get(0)).extracting(MovieDto::getImageFile).isEqualTo(movie.getImageFile());
        assertThat(moviesDto.get(0)).extracting(MovieDto::getTrailer).isEqualTo(movie.getTrailer());
    }
}
