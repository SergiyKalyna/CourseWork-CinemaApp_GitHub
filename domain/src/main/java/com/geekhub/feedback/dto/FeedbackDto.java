package com.geekhub.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FeedbackDto {

    private Long id;
    private LocalDateTime time;
    private String userName;
    private String feedback;
    private int movieScore;
}
