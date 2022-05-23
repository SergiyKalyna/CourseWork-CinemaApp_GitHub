package com.geekub.cinema.web.feedback;

import com.geekhub.feedback.Feedback;
import com.geekhub.feedback.FeedbackConverter;
import com.geekhub.feedback.FeedbackService;
import com.geekhub.feedback.dto.FeedbackCreationDto;
import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import com.geekhub.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(scripts = "classpath:schema.sql")
@WithMockUser(username = "admin", roles = "ADMIN")
class FeedbackControllerTest {

    @Autowired
    FeedbackController feedbackController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    FeedbackService feedbackService;

    @Autowired
    @MockBean
    FeedbackConverter feedbackConverter;

    @Test
    void check_add_feedback_request() throws Exception {
        mockMvc.perform(post("/feedback/1")
                        .param("feedback", "feedback")
                        .param("movieScore", "10")
                        .with(user(new User(1L, "login", "password", Role.USER, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies/1"));

        verify(feedbackService).create(feedbackConverter.convertFromDto(new FeedbackCreationDto()));
    }

    @Test
    void check_delete_feedback_request() throws Exception {
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 2L, "user",
                "feedback", 1);

        when(feedbackService.getFeedback(1L)).thenReturn(feedback);

        mockMvc.perform(post("/feedback/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies/1"));

        verify(feedbackService).delete(1L);
    }

    @Test
    void check_edit_feedback_request() throws Exception {
        FeedbackCreationDto feedbackCreationDto = new FeedbackCreationDto(1L, 1, 2L,
                "feedback", 1);
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 2L, "user",
                "feedback", 1);

        when(feedbackService.getFeedback(1L)).thenReturn(feedback);
        when(feedbackConverter.convertToFeedbackCreationDto(feedback)).thenReturn(feedbackCreationDto);

        mockMvc.perform(get("/feedback/edit/1")
                        .with(user(new User(1L, "login", "password", Role.ADMIN, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("feedback/edit"))
                .andExpect(model().attribute("feedback", feedbackCreationDto));
    }

    @Test()
    void check_edit_feedback_when_user_want_to_change_another_feed() throws Exception {
        mockMvc.perform(get("/feedback/edit/1")
                        .with(user(new User(1L, "login", "password", Role.USER, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(view().name("error"));
    }

    @Test()
    void check_edit_feedback_when_user_want_to_change_own_feed() throws Exception {
        FeedbackCreationDto feedbackCreationDto = new FeedbackCreationDto(1L, 1, 1L,
                "feedback", 1);
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 1L, "user",
                "feedback", 1);

        when(feedbackService.getFeedback(1L)).thenReturn(feedback);
        when(feedbackConverter.convertToFeedbackCreationDto(feedback)).thenReturn(feedbackCreationDto);

        mockMvc.perform(get("/feedback/edit/1")
                        .with(user(new User(1L, "login", "password", Role.USER, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("feedback/edit"))
                .andExpect(model().attribute("feedback", feedbackCreationDto));
    }

    @Test
    void check_response_when_wrong_url() throws Exception {
        mockMvc.perform(get("/feedback/edit/wrongUrl"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void check_update_feedback_request() throws Exception {
        FeedbackCreationDto dto = new FeedbackCreationDto(1L, 1, 2L,
                "feedback", 1);
        Feedback feedback = new Feedback(1L, LocalDateTime.now(), 1, 2L, "user",
                "feedback", 1);

        when(feedbackConverter.convertToFeedbackCreationDto(feedbackService.getFeedback(1L))).thenReturn(dto);
        when(feedbackConverter.convertFromDto(dto)).thenReturn(feedback);

        mockMvc.perform(post("/feedback/update/1")
                        .param("feedback", "feedback")
                        .param("stars", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies/1"));

        verify(feedbackService).update(1L, feedbackConverter.convertFromDto(dto));
    }
}
