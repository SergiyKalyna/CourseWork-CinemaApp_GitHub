package com.geekhub.cinemahall;

import com.geekhub.cinemahall.dto.CinemaHallDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CinemaHallConverterTest {

    @InjectMocks
    CinemaHallConverter cinemaHallConverter;

    @Test
    void toDto_check_to_return_not_null_object() {
        CinemaHall hall = new CinemaHall(1, "Name", 30);

        CinemaHallDto dto = cinemaHallConverter.convertToDto(hall);

        assertNotNull(dto);
    }

    @Test
    void toDto_should_right_convert() {
        CinemaHall hall = new CinemaHall(1, "Name", 30);

        CinemaHallDto dto = cinemaHallConverter.convertToDto(hall);

        assertThat(dto).extracting(CinemaHallDto::getName).isEqualTo(hall.getName());
    }

    @Test
    void fromDto_check_to_return_not_null_object() {
        CinemaHallDto dto = new CinemaHallDto(1, "Name", 30);

        CinemaHall hall = cinemaHallConverter.convertFromDto(dto);

        assertNotNull(hall);
    }

    @Test
    void fromDto_should_right_convert() {
        CinemaHallDto dto = new CinemaHallDto(1, "Name", 30);

        CinemaHall hall = cinemaHallConverter.convertFromDto(dto);

        assertThat(hall).extracting(CinemaHall::getName).isEqualTo(dto.getName());
    }

    @Test
    void convertList_check_return_right_list() {
        CinemaHall hall = new CinemaHall(1, "Name", 30);
        List<CinemaHall> halls = List.of(hall);

        List<CinemaHallDto> dtos = cinemaHallConverter.convertListToDto(halls);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertThat(dtos.get(0).getName()).isEqualTo(hall.getName());
    }


}
