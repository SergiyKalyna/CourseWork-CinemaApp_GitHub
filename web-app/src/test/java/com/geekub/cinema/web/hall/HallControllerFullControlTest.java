package com.geekub.cinema.web.hall;

import com.geekhub.cinemahall.CinemaHall;
import com.geekhub.cinemahall.CinemaHallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(value = "admin", roles = "ADMIN")
@Sql(scripts = "classpath:schema.sql")
public class HallControllerFullControlTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CinemaHallService hallService;

    @Test
    void hall_crud_operation_integration_test() {
        int hallId = createHall().getId();

        editHall(hallId);

        updateHall(hallId);

        delete(hallId);
    }

    private CinemaHall createHall() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("name", "hall");
        parameters.add("capacity", "35");


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/halls/add",
                request,
                String.class
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("Hall is not created")
                .isEqualTo(HttpStatus.FOUND);

        Optional<CinemaHall> cinemaHall = hallService.getAllHalls().stream().findFirst();
        assertThat(cinemaHall).isNotEmpty();

        return cinemaHall.get();
    }

    private void editHall(int hallId) {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/halls/{id}/edit",
                String.class,
                hallId
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("Hall not found")
                .isEqualTo(HttpStatus.OK);
    }

    private void updateHall(int hallId) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("name", "hall");
        parameters.add("capacity", "35");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, HttpHeaders.EMPTY);

        ResponseEntity<String> response = restTemplate.exchange(
                "/halls/{id}/update",
                HttpMethod.POST,
                request,
                String.class,
                hallId
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.FOUND);
    }

    private void delete(int hallId) {
        ResponseEntity<String> response = restTemplate.exchange(
                "/halls/{id}",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                String.class,
                hallId
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.FOUND);
    }
}
