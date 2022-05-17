package com.geekub.cinema.web.menu;

import com.geekhub.movie.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainMenuController.class)
@ContextConfiguration(classes = {DataSource.class, MovieService.class})
class MainMenuControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    MovieService movieService;

    @Test
    void show_root_page() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("index"));
    }

    @Test
    void show_menu_page() throws Exception {
//        mockMvc.perform(get("/menu").with())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
//                .andExpect(view().name("menu/menu-page"));
    }
}
