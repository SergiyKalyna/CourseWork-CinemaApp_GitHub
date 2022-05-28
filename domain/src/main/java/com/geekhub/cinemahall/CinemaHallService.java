package com.geekhub.cinemahall;

import com.geekhub.exception.CinemaHallNotFoundException;
import com.geekhub.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CinemaHallService {

    private static final Logger logger = LoggerFactory.getLogger(CinemaHallService.class);
    private final CinemaHallRepository cinemaHallRepository;

    public CinemaHallService(CinemaHallRepository cinemaHallRepository) {
        this.cinemaHallRepository = cinemaHallRepository;
    }

    public CinemaHall getHall(int id) {
        Optional<CinemaHall> cinemaHall = Optional.ofNullable(cinemaHallRepository.getById(id));
        logger.info(String.format("Cinema hall with id [%s] was got", id));

        return cinemaHall.orElseThrow(() -> new CinemaHallNotFoundException(id));
    }

    public void updateHall(int id, CinemaHall cinemaHall) {
        if (cinemaHallRepository.getById(id) == null) {
            logger.error(String.format("Cinema hall with id [%s] was not found", id));
            throw new CinemaHallNotFoundException(id);
        } else if (cinemaHall.getName() == null || cinemaHall.getName().isBlank()) {
            logger.error("Was not to input name of the hall");
            throw new ValidationException("Was not to input name of the hall");
        } else if (cinemaHall.getCapacity() <= 0) {
            logger.error("Was wrong input capacity of the hall");
            throw new ValidationException("Was wrong input capacity of the hall");
        } else {
            logger.info(String.format("Was updated cinema hall with id [%s]", id));

            cinemaHallRepository.update(id, cinemaHall);
        }
    }

    public int getHallPlaces(int hallId) {
        CinemaHall cinemaHall = cinemaHallRepository.getById(hallId);
        if (cinemaHall == null) {
            throw new CinemaHallNotFoundException(hallId);
        }
        logger.info("Showed hall capacity");
        return cinemaHall.getCapacity();
    }

    public void deleteHall(int id) {
        if (cinemaHallRepository.getById(id) == null) {
            throw new CinemaHallNotFoundException(id);
        }
        logger.info(String.format("Deleted hall with id [%s]", id));
        cinemaHallRepository.delete(id);
    }

    public void addHall(CinemaHall cinemaHall) {
        if (cinemaHall.getName() == null || cinemaHall.getName().isBlank()) {
            throw new ValidationException("Was not to input name of the hall");
        } else if (cinemaHall.getCapacity() <= 0) {
            logger.error("Was wrong input capacity of the hall");
            throw new ValidationException("Was wrong input capacity of the hall");
        }
        logger.info("Was added new hall");
        cinemaHallRepository.add(cinemaHall);
    }

    public List<CinemaHall> getAllHalls() {
        logger.info("Showed all cinema halls");
        return cinemaHallRepository.getAllHalls();
    }
}
