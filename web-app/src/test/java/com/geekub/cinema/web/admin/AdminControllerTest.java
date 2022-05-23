package com.geekub.cinema.web.admin;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(scripts = "classpath:schema.sql")
@WithMockUser(username = "admin", roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    AdminController adminController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    UserService userService;

    @Autowired
    @MockBean
    UserConverter userConverter;

    @Test
    void check_showUsers_request() throws Exception {
        List<UserDto> userDtoList = List.of(new UserDto());
        when(userConverter.convertToListDto(userService.getAllUsers())).thenReturn(userDtoList);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("admin/all-users"))
                .andExpect(model().attribute("users", userDtoList));

        verify(userConverter).convertToListDto(userService.getAllUsers());
    }

    @Test
    void check_delete_user() throws Exception {
        mockMvc.perform(post("/admin/users/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).deleteUser(1L);
    }

    @Test
    void check_request_to_edit_user() throws Exception {
        UserDto user = new UserDto(1L, "name", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        when(userConverter.convertToDto(userService.findById(1L))).thenReturn(user);

        mockMvc.perform(get("/admin/users/1/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("admin/edit"))
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

        mockMvc.perform(post("/admin/users/1/update")
                        .param("role", "User")
                        .param("firstName", "firstName")
                        .param("secondName", "secondName")
                        .param("gender", "Male")
                        .param("birthdayDate", String.valueOf(LocalDate.now())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).updateUser(1L, userConverter.convertFromUserDto(userDto));
    }
}
