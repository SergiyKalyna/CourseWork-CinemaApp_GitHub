package com.geekub.cinema.web.configs;

import com.geekhub.cinemahall.CinemaHallRowMapper;
import com.geekhub.cinemahall.CinemaHallRepository;
import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.EventRowMapper;
import com.geekhub.event.EventRepository;
import com.geekhub.event.EventService;
import com.geekhub.feedback.FeedbackRowMapper;
import com.geekhub.feedback.FeedbackRepository;
import com.geekhub.feedback.FeedbackService;
import com.geekhub.movie.MovieRepository;
import com.geekhub.movie.MovieRowMapper;
import com.geekhub.movie.MovieService;
import com.geekhub.ticket.TicketBookingRepository;
import com.geekhub.ticket.TicketBookingService;
import com.geekhub.ticket.TicketRowMapper;
import com.geekhub.user.UserRowMapper;
import com.geekhub.user.UserRepository;
import com.geekhub.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public MovieRowMapper movieMapper() {
        return new MovieRowMapper();
    }

    @Bean
    public MovieRepository movieRepository(NamedParameterJdbcTemplate jdbcTemplate, MovieRowMapper movieRowMapper) {
        return new MovieRepository(jdbcTemplate, movieRowMapper);
    }

    @Bean
    public MovieService movieService(MovieRepository movieRepository, EventService eventService) {
        return new MovieService(movieRepository, eventService);
    }

    @Bean
    public FeedbackRowMapper feedbackMapper() {
        return new FeedbackRowMapper();
    }

    @Bean
    public FeedbackRepository feedbackRepository(NamedParameterJdbcTemplate jdbcTemplate, FeedbackRowMapper feedbackRowMapper) {
        return new FeedbackRepository(jdbcTemplate, feedbackRowMapper);
    }

    @Bean
    public FeedbackService feedbackService(FeedbackRepository feedbackRepository, MovieService movieService) {
        return new FeedbackService(feedbackRepository, movieService);
    }

    @Bean
    public CinemaHallRowMapper cinemaHallMapper() {
        return new CinemaHallRowMapper();
    }

    @Bean
    public CinemaHallRepository cinemaHallRepository(NamedParameterJdbcTemplate jdbcTemplate, CinemaHallRowMapper cinemaHallRowMapper) {
        return new CinemaHallRepository(jdbcTemplate, cinemaHallRowMapper);
    }

    @Bean
    public CinemaHallService cinemaHallService(CinemaHallRepository cinemaHallRepository) {
        return new CinemaHallService(cinemaHallRepository);
    }

    @Bean
    public EventRowMapper eventMapper() {
        return new EventRowMapper();
    }

    @Bean
    public EventRepository eventRepository(NamedParameterJdbcTemplate jdbcTemplate, EventRowMapper eventRowMapper) {
        return new EventRepository(jdbcTemplate, eventRowMapper);
    }

    @Bean
    public EventService eventService(EventRepository eventRepository, CinemaHallService cinemaHallService) {
        return new EventService(eventRepository, cinemaHallService);
    }

    @Bean
    public TicketRowMapper ticketMapper() {
        return new TicketRowMapper();
    }

    @Bean
    public TicketBookingRepository ticketBookingRepository(NamedParameterJdbcTemplate jdbcTemplate, TicketRowMapper ticketRowMapper) {
        return new TicketBookingRepository(jdbcTemplate, ticketRowMapper);
    }

    @Bean
    public TicketBookingService ticketBookingService(TicketBookingRepository ticketBookingRepository, EventService eventService) {
        return new TicketBookingService(ticketBookingRepository, eventService);
    }

    @Bean
    public UserRowMapper userMapper() {
        return new UserRowMapper();
    }

    @Bean
    public UserRepository userRepository(NamedParameterJdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        return new UserRepository(jdbcTemplate, userRowMapper);
    }

    @Bean
    public UserService userService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new UserService(userRepository, bCryptPasswordEncoder);
    }
}
