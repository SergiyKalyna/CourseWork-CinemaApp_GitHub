package com.geekhub.feedback;

import com.geekhub.feedback.dto.FeedbackCreationDto;
import com.geekhub.feedback.dto.FeedbackDto;
import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import com.geekhub.user.User;
import com.geekhub.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackConverterTest {

    @Mock
    UserService userService;

    @InjectMocks
    FeedbackConverter feedbackConverter;

    @Test
    void convertToFeedbackDto_check_call_service() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 2L, "user",
                "feedback", 1);
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userService.findById(2L)).thenReturn(user);

        feedbackConverter.convertToFeedbackDto(feedback);

        verify(userService).findById(2L);
    }

    @Test
    void convertToFeedbackDto_check_to_right_return() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 2L, "user",
                "feedback", 1);
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        String userName = user.getFirstName() + " " + user.getSecondName();

        when(userService.findById(2L)).thenReturn(user);

        FeedbackDto feedbackDto = feedbackConverter.convertToFeedbackDto(feedback);

        assertThat(feedbackDto).extracting(FeedbackDto::getFeedback).isEqualTo(feedback.getFeedback());
        assertThat(feedbackDto).extracting(FeedbackDto::getId).isEqualTo(feedback.getId());
        assertThat(feedbackDto).extracting(FeedbackDto::getTime).isEqualTo(feedback.getTime());
        assertThat(feedbackDto).extracting(FeedbackDto::getUserName).isEqualTo(userName);
    }

    @Test
    void convertToFeedbackCreationDto_check_to_convert() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 2L, "user",
                "feedback", 1);

        FeedbackCreationDto feedbackCreationDto = feedbackConverter.convertToFeedbackCreationDto(feedback);

        assertThat(feedbackCreationDto).extracting(FeedbackCreationDto::getFeedback).isEqualTo(feedback.getFeedback());
        assertThat(feedbackCreationDto).extracting(FeedbackCreationDto::getId).isEqualTo(feedback.getId());
    }

    @Test
    void convertFromDto_check_call_service_method() {
        FeedbackCreationDto feedbackCreationDto = new FeedbackCreationDto(1L, 1, 2L,
                "feedback", 1);
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userService.findById(2L)).thenReturn(user);

        feedbackConverter.convertFromDto(feedbackCreationDto);

        verify(userService).findById(2L);
    }

    @Test
    void convertFromDto_check_right_convert() {
        FeedbackCreationDto feedbackCreationDto = new FeedbackCreationDto(1L, 1, 2L,
                "feedback", 1);
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        String userName = user.getFirstName() + " " + user.getSecondName();

        when(userService.findById(2L)).thenReturn(user);

        Feedback feedback = feedbackConverter.convertFromDto(feedbackCreationDto);

        assertThat(feedback).extracting(Feedback::getFeedback).isEqualTo(feedbackCreationDto.getFeedback());
        assertThat(feedback).extracting(Feedback::getId).isEqualTo(feedbackCreationDto.getId());
        assertThat(feedback).extracting(Feedback::getUserName).isEqualTo(userName);
    }

    @Test
    void convertToListDto_check_right_convert_list() {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 2L, "user",
                "feedback", 1);
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        List<Feedback> feedbacks = List.of(feedback);

        when(userService.findById(2L)).thenReturn(user);

        List<FeedbackDto> dtoList = feedbackConverter.convertToListDto(feedbacks);

        assertNotNull(dtoList);
        assertThat(dtoList.size()).isEqualTo(1);
        assertThat(dtoList.get(0)).extracting(FeedbackDto::getFeedback).isEqualTo(feedback.getFeedback());
        assertThat(dtoList.get(0)).extracting(FeedbackDto::getMovieScore).isEqualTo(feedback.getMovieScore());
    }
}
