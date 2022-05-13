package com.geekhub.cinemahall;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJdbcTest
class CinemaHallRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Mock
    CinemaHallMapper cinemaHallMapper;

    @Mock
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @InjectMocks
    CinemaHallRepository cinemaHallRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE hall");
    }

    @Test
    void no_history_records_in_db() {
        long notesCount = cinemaHallRepository.getAllHalls().size();
        assertThat(notesCount).isZero();
    }
}
