package com.geekub.cinema.web.ticket;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(value = "admin", roles = "ADMIN")
@Sql(scripts = "classpath:schema.sql")
public class BookTicketIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void ticket_controller_crud_operation_integration_test() {
        create();

        delete();
    }

    private void create() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("place", "1");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/tickets/add/{id}",
                request,
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .withFailMessage("Ticket is not created")
                .isEqualTo(HttpStatus.FOUND);
    }


    private void delete() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/tickets/{id}",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                String.class,
                1
        );

        assertThat(response).extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.FOUND);
    }
}
