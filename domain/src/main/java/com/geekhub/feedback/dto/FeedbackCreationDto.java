package com.geekhub.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackCreationDto {
    private Long id;
    private int movieId;
    private Long userId;
    private String feedback;
    private int movieScore;
}
