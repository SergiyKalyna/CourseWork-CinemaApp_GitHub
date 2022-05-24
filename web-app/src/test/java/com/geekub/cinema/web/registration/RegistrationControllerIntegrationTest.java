package com.geekub.cinema.web.registration;

import com.geekhub.models.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(scripts = "classpath:schema.sql")
public class RegistrationControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void check_registration_work() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("login", "login");
        parameters.add("password", "password");
        parameters.add("confirmPassword", "confirmPassword");
        parameters.add("firstName", "firstName");
        parameters.add("secondName", "secondName");
        parameters.add("gender", String.valueOf(Gender.MALE));
        parameters.add("birthdayDate", String.valueOf(LocalDate.now()));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/registration/add",
                request,
                String.class
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("User is not created")
                .isEqualTo(HttpStatus.FOUND);
    }
}
