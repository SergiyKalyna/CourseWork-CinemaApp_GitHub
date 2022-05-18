package com.geekhub.movie;

import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class Movie {
    private int id;
    private String title;
    private Genre genre;
    private String description;
    private LocalDate release;
    private Production country;
    private List<String> actors;
    private String imageFile;
    private String trailer;
    private int averageRating;
    private byte[] image;

    public Movie() {
    }

    public Movie(int id, String title, Genre genre, String description, LocalDate release, Production country, List<String> actors, String imageFile, String trailer, int averageRating, byte[] image) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.release = release;
        this.country = country;
        this.actors = actors;
        this.imageFile = imageFile;
        this.trailer = trailer;
        this.averageRating = averageRating;
        this.image = image;
    }

    public Movie(int id, String title, Genre genre, String description, LocalDate release, Production country, List<String> actors, String imageFile, String trailer, int averageRating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.release = release;
        this.country = country;
        this.actors = actors;
        this.imageFile = imageFile;
        this.trailer = trailer;
        this.averageRating = averageRating;
    }

    public Movie(int id, String title, Genre genre, String description, LocalDate release, Production country, List<String> actors, String trailer, int averageRating, byte[] image) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.release = release;
        this.country = country;
        this.actors = actors;
        this.trailer = trailer;
        this.averageRating = averageRating;
        this.image = image;
    }
}
