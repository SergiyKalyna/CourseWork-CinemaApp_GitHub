package com.geekub.cinema.web.event;

import com.geekhub.event.dto.EventCreationDto;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(value = "admin", roles = "ADMIN")
@Sql(scripts = "classpath:schema.sql")
public class EventControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void event_crud_operation_integration_test() {
        create();

        get();

        update();

        delete();
    }

    private void create() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("placeCost", "100");
        parameters.add("cinemaHallId", "1");
        parameters.add("time", String.valueOf(LocalDateTime.now()));

        EventCreationDto eventCreationDto = new EventCreationDto();
        eventCreationDto.setPlaceCost(Integer.parseInt(parameters.getFirst("placeCost")));
        eventCreationDto.setCinemaHallId(Integer.parseInt(parameters.getFirst("cinemaHallId")));
        eventCreationDto.setTime(LocalDateTime.parse(parameters.getFirst("time")));

        ResponseEntity<EventCreationDto> response = restTemplate.postForEntity(
                "/events/add/{id}",
                eventCreationDto,
                EventCreationDto.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("Event is not created")
                .isEqualTo(HttpStatus.FOUND);
    }

    private void get() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/events/{id}/edit",
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("Event not found")
                .isEqualTo(HttpStatus.OK);
    }

    private void update() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("placeCost", "100");
        parameters.add("cinemaHallId", "1");
        parameters.add("time", String.valueOf(LocalDateTime.now()));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, HttpHeaders.EMPTY);

        ResponseEntity<String> response = restTemplate.exchange(
                "/events/{id}/update",
                HttpMethod.POST,
                request,
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.FOUND);
    }

    private void delete() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/events/{id}/delete",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.FOUND);
    }
}
