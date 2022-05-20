package com.geekub.cinema.web.menu;

import com.geekhub.models.Gender;
import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import com.geekhub.models.Role;
import com.geekhub.movie.Movie;
import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import com.geekhub.movie.dto.MovieDto;
import com.geekhub.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.sql.DataSource;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainMenuController.class)
@Import(MainMenuController.class)
@ContextConfiguration(classes = DataSource.class)
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
        mockMvc.perform(get("/").with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("index"));
    }

    @Test
    void show_menu_page() throws Exception {
        Movie movie = new Movie();
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        List<Movie> movies = List.of(movie);
        List<MovieDto> dtoMovies = List.of(new MovieDto(1,"title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "image", "trailer", 1));


        when(user.getRole().equals(Role.ADMIN)).thenReturn(true);
        when(movieConverter.convertToListDto(movieService.showLast3Movies())).thenReturn(dtoMovies);

        mockMvc.perform(get("/menu").with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("menu/menu-page"))
                .andExpect(MockMvcResultMatchers.model().attribute("movies", dtoMovies))
                .andExpect(MockMvcResultMatchers.model().attribute("checkUserRights", true));
    }
}
