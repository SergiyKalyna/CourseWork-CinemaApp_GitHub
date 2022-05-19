package com.geekhub.movie.dto;

import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
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
}
