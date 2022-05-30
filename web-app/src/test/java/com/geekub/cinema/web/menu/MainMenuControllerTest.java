package com.geekub.cinema.web.menu;

import com.geekhub.models.Gender;
import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import com.geekhub.models.Role;
import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import com.geekhub.movie.dto.MovieDto;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(username = "admin", roles = "ADMIN")
class MainMenuControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    MovieService movieService;

    @Autowired
    @MockBean
    MovieConverter movieConverter;

    @Test
    void show_root_page() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("index"));
    }

    @Test
    void show_menu_page() throws Exception {
        MovieDto movie = new MovieDto(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1);

        when(movieConverter.convertToListDto(movieService.showLast3Movies())).thenReturn(List.of(movie));

        mockMvc.perform(get("/menu")
                        .with(user(new User(1L, "login", "password", Role.USER, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("menu/menu-page"))
                .andExpect(model().attribute("movies", List.of(movie)))
                .andExpect(model().attribute("checkUserRights", false));

        verify(movieConverter).convertToListDto(movieService.showLast3Movies());
    }
}
