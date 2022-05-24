package com.geekub.cinema.web.user;

import com.geekhub.models.Gender;
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
@WithMockUser(roles = "USER")
@Sql(scripts = "classpath:schema.sql")
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void user_controller_crud_operation_integration_test() {
        get();

        update();
    }

    private void get() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/user/{id}/edit",
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("User not found")
                .isEqualTo(HttpStatus.OK);
    }

    private void update() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("firstName", "firstName");
        parameters.add("secondName", "secondName");
        parameters.add("gender", String.valueOf(Gender.MALE));
        parameters.add("birthdayDate", String.valueOf(LocalDate.now()));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, HttpHeaders.EMPTY);

        ResponseEntity<String> response = restTemplate.exchange(
                "/user/{id}/update",
                HttpMethod.POST,
                request,
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.FOUND);
    }
}
