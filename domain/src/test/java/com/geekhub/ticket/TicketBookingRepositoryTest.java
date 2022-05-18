package com.geekhub.ticket;

import com.geekhub.exception.TicketNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {TicketBookingRepository.class, TicketRowMapper.class})
@Sql(scripts = "classpath:schema.sql")
class TicketBookingRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TicketRowMapper ticketRowMapper;

    @Autowired
    TicketBookingRepository ticketBookingRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE ticket");
    }

    @Test
    void no_history_records_in_db() {
        long ticketCount = ticketBookingRepository.getAll().size();
        assertThat(ticketCount).isZero();
    }

    @Test
    void nothing_happened_when_trying_to_delete_not_existing_ticket() {
        assertThatCode(() -> ticketBookingRepository.deleteTicket(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DirtiesContext
    void check_delete_method() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        assertThat(ticketBookingRepository.getAll().size()).isEqualTo(0);

        ticketBookingRepository.createTicked(ticket);
        assertThat(ticketBookingRepository.getAll().size()).isEqualTo(1);

        ticketBookingRepository.deleteTicketByEventId(1L);
        assertThat(ticketBookingRepository.getAll().size()).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void check_getTickets_by_userId() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);

        assertThat(ticketBookingRepository.getByUserId(1L).size()).isEqualTo(0);

        ticketBookingRepository.createTicked(ticket);

        assertThat(ticketBookingRepository.getByUserId(1L).size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void check_multiply_add_tickets() {
        assertThat(ticketBookingRepository.getAll().size()).isEqualTo(0);
        Ticket ticket1 = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        Ticket ticket2 = new Ticket(2L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);

        ticketBookingRepository.createTicked(ticket1);
        ticketBookingRepository.createTicked(ticket2);

        assertThat(ticketBookingRepository.getAll().size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    void check_deleted_ticket_by_eventId() {
        assertThat(ticketBookingRepository.getAll().size()).isEqualTo(0);
        Ticket ticket1 = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        Ticket ticket2 = new Ticket(2L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 2L, 100);

        ticketBookingRepository.createTicked(ticket1);
        ticketBookingRepository.createTicked(ticket2);

        ticketBookingRepository.deleteTicketByEventId(1L);

        assertThat(ticketBookingRepository.getAll().size()).isEqualTo(1);
    }

    @Test
    void when_ticket_not_found_should_throw_exception() {
        assertThrows(TicketNotFoundException.class, () -> ticketBookingRepository.getById(1L));
    }

    @Test
    @DirtiesContext
    void check_right_return_ticket() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);

        ticketBookingRepository.createTicked(ticket);

        Ticket ticketFromDb = ticketBookingRepository.getById(1L);

        assertThat(ticketFromDb).extracting(Ticket::getMovieName).isEqualTo(ticket.getMovieName());
        assertThat(ticketFromDb).extracting(Ticket::getId).isEqualTo(ticket.getId());
    }
}
