package com.geekhub.event;

import com.geekhub.exception.EventNotFoundException;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {EventRepository.class, EventRowMapper.class})
@Sql(scripts = "classpath:schema.sql")
class EventRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    EventRowMapper eventRowMapper;

    @Autowired
    EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE event");
    }

    @Test
    void no_history_records_in_db() {
        long eventCount = eventRepository.getAllEvents().size();
        assertThat(eventCount).isZero();
    }

    @Test
    void nothing_happened_when_trying_to_delete_not_existing_event() {
        assertThatCode(() -> eventRepository.delete(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DirtiesContext
    void check_result_of_delete() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 6, 100);
        eventRepository.addEvent(event);

        assertThat(eventRepository.getAllEvents().size()).isEqualTo(1);
        List<Event> events = eventRepository.getAllEvents();

        eventRepository.delete(1L);

        assertThat(eventRepository.getAllEvents().size()).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void check_result_of_getAllEvents() {
        assertThat(eventRepository.getAllEvents().size()).isEqualTo(0);

        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 6, 100);
        Event event1 = new Event(1L, LocalDateTime.of(2022, 1, 10, 11, 1, 22), 1, 1, 6, 100);

        eventRepository.addEvent(event);
        eventRepository.addEvent(event1);

        assertThat(eventRepository.getAllEvents().size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    void check_search_by_movieId() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 6, 100);
        eventRepository.addEvent(event);

        assertThat(eventRepository.getAllEvents().size()).isEqualTo(1);

        assertThat(eventRepository.findEventsByMovieId(1).size()).isEqualTo(1);
    }

    @Test
    void when_event_is_not_found() {
        assertThrows(EventNotFoundException.class, () -> eventRepository.getEvent(1L));
    }

    @Test
    @DirtiesContext
    void success_found_event() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 6, 100);
        eventRepository.addEvent(event);

        Event actualEvent = eventRepository.getEvent(1L);

        assertThat(actualEvent).extracting(Event::getId).isEqualTo(1L);
        assertThat(actualEvent).extracting(Event::getTime).isEqualTo(event.getTime());
    }

    @Test
    @DirtiesContext
    void success_add_event() {
        assertThat(eventRepository.getAllEvents().size()).isEqualTo(0);

        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 6, 100);
        eventRepository.addEvent(event);

        assertThat(eventRepository.getAllEvents().size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void check_update_event_method() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 6, 100);
        LocalDateTime updateTime = LocalDateTime.of(2022, 1, 10, 11, 1, 22);

        eventRepository.addEvent(event);
        event.setTime(updateTime);

        eventRepository.updateEvent(1L, event);
        Event updatedEvent = eventRepository.getEvent(1L);

        assertThat(updatedEvent).extracting(Event::getTime).isEqualTo(updateTime);
    }

    @Test
    @DirtiesContext
    void check_getAllByDate_method_result() {
        String date = "2022-05-16T22:00";
        assertThat(eventRepository.getAllByDate(date).size()).isEqualTo(0);

        Event event = new Event(1L, LocalDateTime.of(2022, 5, 16, 22, 10, 1), 1, 1, 6, 100);
        Event event1 = new Event(2L, LocalDateTime.of(2022, 5, 16, 11, 1, 22), 1, 1, 6, 100);

        eventRepository.addEvent(event);
        eventRepository.addEvent(event1);

        assertThat(eventRepository.getAllByDate(date).size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    void check_getAllByDateTime_method_result() {
        LocalDateTime date = LocalDateTime.parse("2022-05-16T22:00");
        assertThat(eventRepository.getAllByDateTime(date).size()).isEqualTo(0);

        Event event = new Event(1L, LocalDateTime.of(2022, 5, 16, 22, 0, 0),
                1, 1, 6, 100);
        Event event1 = new Event(2L, LocalDateTime.of(2022, 5, 16, 11, 1, 22),
                1, 1, 6, 100);

        eventRepository.addEvent(event);
        eventRepository.addEvent(event1);

        assertThat(eventRepository.getAllByDateTime(date).size()).isEqualTo(1);
    }
}
