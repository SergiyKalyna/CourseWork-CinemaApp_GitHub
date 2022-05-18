package com.geekhub.feedback;

import com.geekhub.feedback.dto.FeedbackCreationDto;
import com.geekhub.feedback.dto.FeedbackDto;
import com.geekhub.user.User;
import com.geekhub.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackConverter {

    private final UserService userService;

    public FeedbackConverter(UserService userService) {
        this.userService = userService;
    }

    public FeedbackDto convertToFeedbackDto(Feedback feedback) {
        User user = userService.findById(feedback.getUserId());

        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setId(feedback.getId());
        feedbackDto.setTime(feedback.getTime());
        feedbackDto.setUserName(user.getFirstName() + " " + user.getSecondName());
        feedbackDto.setFeedback(feedback.getFeedback());
        feedbackDto.setMovieScore(feedback.getMovieScore());

        return feedbackDto;
    }

    public FeedbackCreationDto convertToFeedbackCreationDto(Feedback feedback) {
        FeedbackCreationDto dto = new FeedbackCreationDto();

        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUserId());
        dto.setMovieId(feedback.getMovieId());
        dto.setFeedback(feedback.getFeedback());
        dto.setMovieScore(feedback.getMovieScore());

        return dto;
    }

    public Feedback convertFromDto(FeedbackCreationDto feedbackCreationDto) {
        User user = userService.findById(feedbackCreationDto.getUserId());
        Feedback feedback = new Feedback();

        feedback.setId(feedbackCreationDto.getId());
        feedback.setUserId(feedbackCreationDto.getUserId());
        feedback.setMovieId(feedbackCreationDto.getMovieId());
        feedback.setFeedback(feedbackCreationDto.getFeedback());
        feedback.setMovieScore(feedbackCreationDto.getMovieScore());
        feedback.setUserName(user.getFirstName() + " " + user.getSecondName());

        return feedback;
    }

    public List<FeedbackDto> convertToListDto(List<Feedback> feedbacks) {
        return feedbacks.stream().map(this::convertToFeedbackDto).collect(Collectors.toList());
    }
}
