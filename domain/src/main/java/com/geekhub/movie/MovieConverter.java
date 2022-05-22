package com.geekhub.movie;

import com.geekhub.movie.dto.MovieCreateDto;
import com.geekhub.movie.dto.MovieDto;
import com.geekhub.movie.dto.MovieDtoForEvent;

import java.util.List;
import java.util.stream.Collectors;

public class MovieConverter {

    public Movie convertFromDto(MovieCreateDto movieCreateDto) {
        Movie movie = new Movie();
        movie.setId(movieCreateDto.getId());
        movie.setTitle(movieCreateDto.getTitle());
        movie.setGenre(movieCreateDto.getGenre());
        movie.setCountry(movieCreateDto.getCountry());
        movie.setRelease(movieCreateDto.getRelease());
        movie.setDescription(movieCreateDto.getDescription());
        movie.setTrailer(movieCreateDto.getTrailer());
        movie.setImage(movieCreateDto.getImage());
        movie.setActors(movieCreateDto.getActors());
        movie.setAverageRating(movieCreateDto.getAverageRating());

        return movie;
    }

    public MovieDto convertToMovieDto(Movie movie) {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(movie.getId());
        movieDto.setTitle(movie.getTitle());
        movieDto.setGenre(movie.getGenre());
        movieDto.setCountry(movie.getCountry());
        movieDto.setRelease(movie.getRelease());
        movieDto.setDescription(movie.getDescription());
        movieDto.setTrailer(movie.getTrailer());
        movieDto.setImageFile(movie.getImageFile());
        movieDto.setActors(movie.getActors());
        movieDto.setAverageRating(movie.getAverageRating());

        return movieDto;
    }

    public MovieCreateDto convertToMovieCreateDto(Movie movie) {
        MovieCreateDto movieDto = new MovieCreateDto();
        movieDto.setId(movie.getId());
        movieDto.setTitle(movie.getTitle());
        movieDto.setGenre(movie.getGenre());
        movieDto.setCountry(movie.getCountry());
        movieDto.setRelease(movie.getRelease());
        movieDto.setDescription(movie.getDescription());
        movieDto.setTrailer(movie.getTrailer());
        movieDto.setImage(movie.getImage());
        movieDto.setActors(movie.getActors());
        movieDto.setAverageRating(movie.getAverageRating());

        return movieDto;
    }

    public MovieDtoForEvent convertToDto(Movie movie) {
        MovieDtoForEvent movieDtoForEvent = new MovieDtoForEvent();
        movieDtoForEvent.setId(movie.getId());
        movieDtoForEvent.setTitle(movie.getTitle());

        return movieDtoForEvent;
    }

    public List<MovieDto> convertToListDto(List<Movie> movies) {
        return movies.stream().map(this::convertToMovieDto).collect(Collectors.toList());
    }
}
