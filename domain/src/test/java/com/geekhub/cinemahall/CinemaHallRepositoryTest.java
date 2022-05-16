package com.geekhub.cinemahall;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {CinemaHallRepository.class,CinemaHallMapper.class})
@Sql(scripts = "classpath:schema.sql")
class CinemaHallRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CinemaHallMapper cinemaHallMapper;

    @Autowired
    CinemaHallRepository cinemaHallRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE hall");
    }

    @Test
    void no_history_records_in_db() {
        long hallCount = cinemaHallRepository.getAllHalls().size();
        System.out.println(hallCount);
    }
}
