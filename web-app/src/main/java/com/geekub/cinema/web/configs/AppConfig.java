package com.geekub.cinema.web.configs;

import com.geekhub.cinemahall.CinemaHallMapper;
import com.geekhub.cinemahall.CinemaHallRepository;
import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.EventMapper;
import com.geekhub.event.EventRepository;
import com.geekhub.event.EventService;
import com.geekhub.feedback.FeedbackMapper;
import com.geekhub.feedback.FeedbackRepository;
import com.geekhub.feedback.FeedbackService;
import com.geekhub.movie.MovieRepository;
import com.geekhub.movie.MovieRowMapper;
import com.geekhub.movie.MovieService;
import com.geekhub.ticket.TicketBookingRepository;
import com.geekhub.ticket.TicketBookingService;
import com.geekhub.ticket.TicketMapper;
import com.geekhub.user.UserMapper;
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
    public FeedbackMapper feedbackMapper() {
        return new FeedbackMapper();
    }

    @Bean
    public FeedbackRepository feedbackRepository(NamedParameterJdbcTemplate jdbcTemplate, FeedbackMapper feedbackMapper) {
        return new FeedbackRepository(jdbcTemplate, feedbackMapper);
    }

    @Bean
    public FeedbackService feedbackService(FeedbackRepository feedbackRepository, MovieService movieService) {
        return new FeedbackService(feedbackRepository, movieService);
    }

    @Bean
    public CinemaHallMapper cinemaHallMapper() {
        return new CinemaHallMapper();
    }

    @Bean
    public CinemaHallRepository cinemaHallRepository(NamedParameterJdbcTemplate jdbcTemplate, CinemaHallMapper cinemaHallMapper) {
        return new CinemaHallRepository(jdbcTemplate, cinemaHallMapper);
    }

    @Bean
    public CinemaHallService cinemaHallService(CinemaHallRepository cinemaHallRepository) {
        return new CinemaHallService(cinemaHallRepository);
    }

    @Bean
    public EventMapper eventMapper() {
        return new EventMapper();
    }

    @Bean
    public EventRepository eventRepository(NamedParameterJdbcTemplate jdbcTemplate, EventMapper eventMapper) {
        return new EventRepository(jdbcTemplate, eventMapper);
    }

    @Bean
    public EventService eventService(EventRepository eventRepository, CinemaHallService cinemaHallService) {
        return new EventService(eventRepository, cinemaHallService);
    }

    @Bean
    public TicketMapper ticketMapper() {
        return new TicketMapper();
    }

    @Bean
    public TicketBookingRepository ticketBookingRepository(NamedParameterJdbcTemplate jdbcTemplate, TicketMapper ticketMapper) {
        return new TicketBookingRepository(jdbcTemplate, ticketMapper);
    }

    @Bean
    public TicketBookingService ticketBookingService(TicketBookingRepository ticketBookingRepository, EventService eventService) {
        return new TicketBookingService(ticketBookingRepository, eventService);
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public UserRepository userRepository(NamedParameterJdbcTemplate jdbcTemplate, UserMapper userMapper) {
        return new UserRepository(jdbcTemplate, userMapper);
    }

    @Bean
    public UserService userService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return new UserService(userRepository, bCryptPasswordEncoder);
    }
}
