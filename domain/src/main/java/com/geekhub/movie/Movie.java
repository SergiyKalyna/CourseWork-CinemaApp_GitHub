package com.geekhub.movie;

import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

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
    private String imageName;
    private String trailer;
    private int averageRating;

    public Movie() {
    }

    public Movie(int id, String title, Genre genre, String description, LocalDate release, Production country, List<String> actors, String imageName, String trailer, int averageRating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.release = release;
        this.country = country;
        this.actors = actors;
        this.imageName = imageName;
        this.trailer = trailer;
        this.averageRating = averageRating;
    }

    @Transient
    public String getPhotosImagePath() {
        return "/web-app/src/main/resources/static/images/" + imageName;
    }
}
