package com.geekub.cinema.web.hall;

import com.geekhub.cinemahall.CinemaHall;
import com.geekhub.cinemahall.CinemaHallConverter;
import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.cinemahall.dto.CinemaHallDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.List;

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
class HallControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    CinemaHallService cinemaHallService;

    @Autowired
    @MockBean
    CinemaHallConverter cinemaHallConverter;

    @Test
    void show_all_halls_request() throws Exception {
        List<CinemaHall> cinemaHalls = List.of(new CinemaHall(1, "name", 35));
        List<CinemaHallDto> halls = List.of(new CinemaHallDto(1, "name", 35));

        when(cinemaHallService.getAllHalls()).thenReturn(cinemaHalls);
        when(cinemaHallConverter.convertListToDto(cinemaHalls)).thenReturn(halls);

        mockMvc.perform(get("/halls").with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("hall/all-halls"))
                .andExpect(model().attribute("halls", halls));
    }

    @Test
    void check_response_when_wrong_url() throws Exception {
        mockMvc.perform(get("/halls/wrongUrl/edit").with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void check_get_hall_request() throws Exception {
        when(cinemaHallConverter.convertToDto(cinemaHallService.getHall(1))).thenReturn(new CinemaHallDto());

        mockMvc.perform(get("/halls/1/edit").with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("hall/edit"))
                .andExpect(model().attribute("cinemaHall", new CinemaHallDto()));
    }

    @Test
    void check_updated_halls_by_id() throws Exception {
        CinemaHallDto cinemaHall = new CinemaHallDto(1, "hall", 35);
        CinemaHall hall = new CinemaHall(1, "hall", 35);

        when(cinemaHallConverter.convertFromDto(cinemaHall)).thenReturn(hall);

        mockMvc.perform(post("/halls/1/update")
                        .param("name", "hall")
                        .param("capacity", "35")
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/halls"));

        verify(cinemaHallService).updateHall(1, cinemaHallConverter.convertFromDto(cinemaHall));
    }

    @Test
    void delete_hall() throws Exception {
        mockMvc.perform(post("/halls/1").with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/halls"));

        verify(cinemaHallService).deleteHall(1);
    }

    @Test
    void create_hall_check_view() throws Exception {
        mockMvc.perform(get("/halls/create").with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("hall/create"))
                .andExpect(model().attribute("cinemaHall", new CinemaHallDto()));
    }

    @Test
    void check_created_hall() throws Exception {
        mockMvc.perform(post("/halls/add")
                        .param("name", "hall")
                        .param("capacity", "35")
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/halls"));

        verify(cinemaHallService).addHall(cinemaHallConverter.convertFromDto(new CinemaHallDto()));
    }
}
