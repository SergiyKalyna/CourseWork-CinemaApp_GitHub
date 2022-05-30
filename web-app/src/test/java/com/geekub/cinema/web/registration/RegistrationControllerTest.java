package com.geekub.cinema.web.registration;

import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import com.geekhub.user.User;
import com.geekhub.user.UserConverter;
import com.geekhub.user.UserService;
import com.geekhub.user.dto.UserCreateDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(username = "admin", roles = "ADMIN")
class RegistrationControllerTest {

    @Autowired
    RegistrationController registrationController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    UserService userService;

    @Autowired
    @MockBean
    UserConverter userConverter;


    @Test
    void doRegister_show_registration_view() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("registration/registration"))
                .andExpect(model().attribute("user", new UserCreateDto()));
    }

    @Test
    void addUser_check_status_isOk() throws Exception {
        mockMvc.perform(post("/registration/add")
                        .param("login", "login")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .param("firstName", "firstName")
                        .param("secondName", "secondName")
                        .param("birthdayDate", String.valueOf(LocalDate.now()))
                        .param("gender", String.valueOf(Gender.MALE)))
                .andExpect(status().isOk());
    }

    @Test
    void addUser_when_one_of_attribute_is_empty_or_blank_should_return_back() throws Exception {
        mockMvc.perform(post("/registration/add")
                        .param("login", "")
                        .param("password", "")
                        .param("confirmPassword", "password")
                        .param("firstName", "firstName")
                        .param("secondName", "secondName")
                        .param("birthdayDate", String.valueOf(LocalDate.now()))
                        .param("gender", String.valueOf(Gender.MALE)))
                .andExpect(view().name("registration/registration"));
    }

    @Test
    void addUser_check_call_service_to_create() throws Exception {
        UserCreateDto userDto = new UserCreateDto("login", "password", "password", "first name",
                "second name");
        User user = new User(1L, "login", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userConverter.convertFromUserCreateDto(userDto)).thenReturn(user);

        mockMvc.perform(post("/registration/add")
                        .flashAttr("user", userDto)
                        .param("birthdayDate", String.valueOf(LocalDate.now()))
                        .param("gender", String.valueOf(Gender.MALE)))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu"));

        verify(userService).saveUser(user);
    }
}
