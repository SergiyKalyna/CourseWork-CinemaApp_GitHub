package com.geekub.cinema.web.user;

import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import com.geekhub.user.User;
import com.geekhub.user.UserConverter;
import com.geekhub.user.UserService;
import com.geekhub.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(username = "admin", roles = "ADMIN")
class UserControllerTest {

    @Autowired
    UserController userController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    UserService userService;

    @Autowired
    @MockBean
    UserConverter userConverter;

    @Test
    void getProfile_check_show_view() throws Exception {
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        UserDto userDto = new UserDto(1L, "name", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userConverter.convertToDto(userService.findById(1L))).thenReturn(userDto);

        mockMvc.perform(get("/user/profile")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("user/profile"))
                .andExpect(model().attribute("user", userDto));
    }

    @Test
    void editPassword_check_show_view() throws Exception {
        mockMvc.perform(get("/user/1/edit_password"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("user/edit-password"))
                .andExpect(model().attribute("id", 1L));
    }

    @Test
    void updatePassword_when_newPassword_and_confirmPassword_is_not_matches() throws Exception {
        mockMvc.perform(post("/user/1/update_password")
                        .param("oldPassword", "oldPassword")
                        .param("newPassword", "password1")
                        .param("confirmPassword", "password"))
                .andExpect(view().name("error"));
    }

    @Test
    void updatePassword_check_success() throws Exception {
        mockMvc.perform(post("/user/1/update_password")
                        .param("oldPassword", "oldPassword")
                        .param("newPassword", "password")
                        .param("confirmPassword", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));

        verify(userService).changePassword(1L, "oldPassword", "password");
    }

    @Test
    void editUser_when_wrong_url() throws Exception {
        mockMvc.perform(get("/user/url/edit"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void check_request_to_edit_user() throws Exception {
        UserDto user = new UserDto(1L, "name", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userConverter.convertToDto(userService.findById(1L))).thenReturn(user);

        mockMvc.perform(get("/user/1/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("user/edit"))
                .andExpect(model().attribute("user", user));

        verify(userConverter).convertToDto(userService.findById(1L));
    }

    @Test
    void check_update_user_request() throws Exception {
        User user = new User(1L, "login", "password", Role.USER, "firstName",
                "secondName", Gender.MALE, LocalDate.now());
        UserDto userDto = new UserDto();

        when(userConverter.convertToDto(userService.findById(1L))).thenReturn(userDto);
        when(userConverter.convertFromUserDto(userDto)).thenReturn(user);

        mockMvc.perform(post("/user/1/update")
                        .param("firstName", "firstName")
                        .param("secondName", "secondName")
                        .param("gender", "Male")
                        .param("birthdayDate", String.valueOf(LocalDate.now())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));

        verify(userService).updateUser(1L, userConverter.convertFromUserDto(userDto));
    }
}
