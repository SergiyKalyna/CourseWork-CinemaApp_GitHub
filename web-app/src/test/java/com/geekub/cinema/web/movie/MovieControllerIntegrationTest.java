package com.geekub.cinema.web.movie;

import com.geekhub.models.Genre;
import com.geekhub.models.Production;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(value = "admin", roles = "ADMIN")
@Sql(scripts = "classpath:schema.sql")
public class MovieControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void movie_crud_operation_integration_test() {
        create();

        get();

        update();

        delete();
    }

    private void create() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("title", "title");
        parameters.add("description", "description");
        parameters.add("release", String.valueOf(LocalDate.now()));
        parameters.add("country", String.valueOf(Production.USA));
        parameters.add("genre", String.valueOf(Genre.DRAMA));
        parameters.add("actors", "actors");
        parameters.add("trailer", "trailer");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/movies/create-movie/add",
                request,
                String.class
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("Movie is not created")
                .isEqualTo(HttpStatus.FOUND);
    }

    private void get() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/movies/{id}",
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("Movie not found")
                .isEqualTo(HttpStatus.OK);
    }

    private void update() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("title", "title");
        parameters.add("description", "description");
        parameters.add("release", String.valueOf(LocalDate.now()));
        parameters.add("country", String.valueOf(Production.USA));
        parameters.add("genre", String.valueOf(Genre.DRAMA));
        parameters.add("actors", "actors");
        parameters.add("trailer", "trailer");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, HttpHeaders.EMPTY);

        ResponseEntity<String> response = restTemplate.exchange(
                "/movies/{id}/update",
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
                "/movies/{id}",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.FOUND);
    }
}
