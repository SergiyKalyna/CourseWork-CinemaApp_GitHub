package com.geekhub.cinemahall;

import com.geekhub.exception.CinemaHallNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {CinemaHallRepository.class, CinemaHallRowMapper.class})
@Sql(scripts = "classpath:schema.sql")
class CinemaHallRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CinemaHallRowMapper cinemaHallRowMapper;

    @Autowired
    CinemaHallRepository cinemaHallRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE hall");
    }

    @Test
    void no_history_records_in_db() {
        long hallCount = cinemaHallRepository.getAllHalls().size();
        assertThat(hallCount).isZero();
    }

    @Test
    void nothing_happened_when_trying_to_delete_not_existing_hall() {
        assertThatCode(() -> cinemaHallRepository.delete(1))
                .doesNotThrowAnyException();
    }

    @Test
    void check_success_delete_hall() {
        CinemaHall cinemaHall = new CinemaHall(4, "Hall", 60);

        cinemaHallRepository.add(cinemaHall);

        assertThat(cinemaHallRepository.getAllHalls().size()).isEqualTo(1);

        cinemaHallRepository.delete(4);

        assertThat(cinemaHallRepository.getAllHalls().size()).isEqualTo(0);
    }


    @Test
    void success_to_add_hall() {
        CinemaHall cinemaHall = new CinemaHall(4, "Hall", 60);

        cinemaHallRepository.add(cinemaHall);

        assertThat(cinemaHallRepository.getAllHalls().size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void check_result_get_all_method() {
        CinemaHall cinemaHall = new CinemaHall(1, "Hall", 60);

        assertThat(cinemaHallRepository.getAllHalls().size() == 0);
        cinemaHallRepository.add(cinemaHall);

        int expected = 1;
        int actual = cinemaHallRepository.getAllHalls().size();

        assertEquals(expected, actual);
    }

    @Test
    void check_result_add_multiply_hall() {
        CinemaHall cinemaHall1 = new CinemaHall(1, "Hall", 60);
        CinemaHall cinemaHall2 = new CinemaHall(2, "Hal", 60);

        assertThat(cinemaHallRepository.getAllHalls().size() == 0);
        cinemaHallRepository.add(cinemaHall1);
        cinemaHallRepository.add(cinemaHall2);

        int expected = 2;
        int actual = cinemaHallRepository.getAllHalls().size();

        assertEquals(expected, actual);
    }

    @Test
    void if_hall_was_not_found() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallRepository.getById(1));
    }

    @Test
    @DirtiesContext
    void when_hall_success_found() {
        CinemaHall cinemaHall = new CinemaHall(4, "Hall", 60);

        cinemaHallRepository.add(cinemaHall);

        assertEquals(1, cinemaHallRepository.getAllHalls().size());

        CinemaHall actual = cinemaHallRepository.getById(4);

        assertEquals(cinemaHall.getName(), actual.getName());
        assertEquals(cinemaHall.getCapacity(), actual.getCapacity());
    }

    @Test
    @DirtiesContext
    void check_success_method_to_update() {
        CinemaHall cinemaHall = new CinemaHall(4, "Hall", 60);

        cinemaHallRepository.add(cinemaHall);
        cinemaHall.setName("update hall");
        cinemaHall.setCapacity(65);

        cinemaHallRepository.update(4, cinemaHall);
        CinemaHall actualCinemaHall = cinemaHallRepository.getById(4);

        assertEquals(cinemaHall.getName(), actualCinemaHall.getName());
        assertEquals(65, actualCinemaHall.getCapacity());
    }
}
