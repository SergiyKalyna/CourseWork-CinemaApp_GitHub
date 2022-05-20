package com.geekub.cinema.web.menu;

import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
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
        mockMvc.perform(get("/menu"))
                .andExpect(status().isOk());

        verify(movieService).showLast3Movies();
        verify(movieConverter).convertToListDto(movieService.showLast3Movies());
    }
}
