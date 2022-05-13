package com.geekhub.cinemahall;

import com.geekhub.exception.CinemaHallNotFoundException;
import com.geekhub.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CinemaHallServiceTest {

    @Mock
    CinemaHallRepository cinemaHallRepository;

    @InjectMocks
    CinemaHallService cinemaHallService;

    @Test
    void getHall_check_when_input_number_less_than_0() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHall(-1));
    }

    @Test
    void getHall_check_when_input_number_is_0() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHall(0));
    }

    @Test
    void getHall_check_when_was_input_wrong_number() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHall(30));
    }

    @Test
    void getHall_when_return_null() {
        CinemaHall hall = null;
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHall(1));
    }

    @Test
    void check_to_call_method_get_hall() {
        CinemaHall hall = new CinemaHall(1, "Black hall", 60);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        cinemaHallService.getHall(1);

        verify(cinemaHallRepository).getById(1);
    }

    @Test
    void check_to_return_right_hall() {
        CinemaHall hall = new CinemaHall(1, "Black hall", 60);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        CinemaHall actual = cinemaHallService.getHall(1);

        assertEquals(hall, actual);
    }

    @Test
    void getAllHalls_check_call_method() {
        List<CinemaHall> halls = new ArrayList<>();
        when(cinemaHallRepository.getAllHalls()).thenReturn(halls);

        cinemaHallService.getAllHalls();

        verify(cinemaHallRepository).getAllHalls();
    }

    @Test
    void getAllHalls_check_result() {
        List<CinemaHall> expected = new ArrayList<>();
        when(cinemaHallRepository.getAllHalls()).thenReturn(expected);

        List<CinemaHall> actual = cinemaHallService.getAllHalls();

        assertEquals(expected, actual);
    }

    @Test
    void getAllHalls_check_size() {
        List<CinemaHall> expected = new ArrayList<>();
        when(cinemaHallRepository.getAllHalls()).thenReturn(expected);

        int actualSize = cinemaHallService.getAllHalls().size();

        assertEquals(0, actualSize);
    }

    @Test
    void delete_when_id_less_than_0() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHall(-1));
    }

    @Test
    void delete_when_id_is_0() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHall(0));
    }

    @Test
    void delete_when_id_more_than_size() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHall(30));
    }

    @Test
    void delete_when_object_is_null() {
        CinemaHall hall = null;
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.deleteHall(1));
    }

    @Test
    void delete_check_call_methods() {
        CinemaHall hall = new CinemaHall(1, "Black hall", 60);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        cinemaHallService.deleteHall(1);

        verify(cinemaHallRepository).getById(1);
        verify(cinemaHallRepository).delete(1);
    }

    @Test
    void getHallPlaces_when_id_less_than_0() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHallPlaces(-1));
    }

    @Test
    void getHallPlaces_when_id_is_0() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHallPlaces(0));
    }

    @Test
    void getHallPlaces_when_id_more_than_size() {
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.getHallPlaces(30));
    }

    @Test
    void getHallPlaces_check_call_method() {
        CinemaHall hall = new CinemaHall(1, "Black hall", 60);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        cinemaHallService.getHallPlaces(1);

        verify(cinemaHallRepository).getById(1);
    }

    @Test
    void getHallPlaces_when_valid_id() {
        CinemaHall hall = new CinemaHall(1, "Black hall", 60);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        int actualCapacity = cinemaHallService.getHallPlaces(1);

        assertEquals(60, actualCapacity);
    }

    @Test
    void add_cinema_hall_when_name_is_null() {
        CinemaHall hall = new CinemaHall(1, null, 60);

        assertThrows(ValidationException.class, () -> cinemaHallService.addHall(hall));
    }

    @Test
    void add_cinema_hall_when_name_is_empty() {
        CinemaHall hall = new CinemaHall(1, "", 60);

        assertThrows(ValidationException.class, () -> cinemaHallService.addHall(hall));
    }

    @Test
    void add_cinema_hall_when_capacity_less_than_0() {
        CinemaHall hall = new CinemaHall(1, "Name", -1);

        assertThrows(ValidationException.class, () -> cinemaHallService.addHall(hall));
    }

    @Test
    void add_cinema_hall_when_capacity_is_0() {
        CinemaHall hall = new CinemaHall(1, "Name", 0);

        assertThrows(ValidationException.class, () -> cinemaHallService.addHall(hall));
    }

    @Test
    void add_cinema_hall_call_method() {
        CinemaHall hall = new CinemaHall(1, "Name", 30);
        cinemaHallService.addHall(hall);

        verify(cinemaHallRepository).add(hall);
    }

    @Test
    void add_cinema_hall_when_valid_fields() {
        CinemaHall hall = new CinemaHall(1, "Name", 30);

        assertDoesNotThrow(() -> cinemaHallService.addHall(hall));
    }

    @Test
    void update_when_hallToUpdate_was_not_found() {
        CinemaHall hall = new CinemaHall(1, "Name", 30);

        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.updateHall(0, hall));
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.updateHall(-1, hall));
        assertThrows(CinemaHallNotFoundException.class, () -> cinemaHallService.updateHall(30, hall));
    }

    @Test
    void update_call_method_to_find_hall() {
        CinemaHall hall = new CinemaHall(1, "Black hall", 60);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        cinemaHallService.updateHall(1, hall);

        verify(cinemaHallRepository).getById(1);
    }

    @Test
    void update_when_name_is_null() {
        CinemaHall hall = new CinemaHall(1, null, 60);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        assertThrows(ValidationException.class, () -> cinemaHallService.updateHall(1, hall));
    }

    @Test
    void update_when_name_is_empty() {
        CinemaHall hall = new CinemaHall(1, "", 60);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        assertThrows(ValidationException.class, () -> cinemaHallService.updateHall(1, hall));
    }

    @Test
    void update_when_capacity_is_0() {
        CinemaHall hall = new CinemaHall(1, "Name", 0);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        assertThrows(ValidationException.class, () -> cinemaHallService.updateHall(1, hall));
    }

    @Test
    void update_when_capacity_less_than_0() {
        CinemaHall hall = new CinemaHall(1, "Name", -1);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        assertThrows(ValidationException.class, () -> cinemaHallService.updateHall(1, hall));
    }

    @Test
    void update_cinema_hall_call_repository_method() {
        CinemaHall hall = new CinemaHall(1, "Name", 30);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        cinemaHallService.updateHall(1, hall);

        verify(cinemaHallRepository).update(1, hall);
    }

    @Test
    void update_cinema_hall_when_successful() {
        CinemaHall hall = new CinemaHall(1, "Name", 30);
        when(cinemaHallRepository.getById(1)).thenReturn(hall);

        assertDoesNotThrow(() -> cinemaHallService.updateHall(1, hall));
    }

}
