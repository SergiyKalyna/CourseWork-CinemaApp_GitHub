package com.geekhub.user;

import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import com.geekhub.user.dto.UserCreateDto;
import com.geekhub.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    @InjectMocks
    UserConverter userConverter;

    @Test
    void convertToDto_check_right_convert() {
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        UserDto userDto = userConverter.convertToDto(user);

        assertThat(userDto).extracting(UserDto::getId).isEqualTo(user.getId());
        assertThat(userDto).extracting(UserDto::getLogin).isEqualTo(user.getLogin());
    }

    @Test
    void check_convert_to_list_dto() {
        User user = new User(2L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        List<User> users = List.of(user);

        List<UserDto> usersDto = userConverter.convertToListDto(users);

        assertEquals(1, usersDto.size());
        assertThat(usersDto.get(0)).extracting(UserDto::getId).isEqualTo(user.getId());
        assertThat(usersDto.get(0)).extracting(UserDto::getLogin).isEqualTo(user.getLogin());
        assertThat(usersDto.get(0)).extracting(UserDto::getBirthdayDate).isEqualTo(user.getBirthdayDate());
    }

    @Test
    void convertFromUserCreateDto_check_to_right_convert() {
        UserCreateDto userCreateDto = new UserCreateDto("name", "password", "first name",
                "second name", Gender.MALE, LocalDate.now());

        User user = userConverter.convertFromUserCreateDto(userCreateDto);

        assertThat(user).extracting(User::getPassword).isEqualTo(userCreateDto.getPassword());
        assertThat(user).extracting(User::getBirthdayDate).isEqualTo(userCreateDto.getBirthdayDate());
        assertThat(user).extracting(User::getGender).isEqualTo(userCreateDto.getGender());
    }

    @Test
    void convertFromUserDto_check_to_right_convert() {
        UserDto userDto = new UserDto(2L, "name", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());

        User user = userConverter.convertFromUserDto(userDto);

        assertThat(user).extracting(User::getId).isEqualTo(userDto.getId());
        assertThat(user).extracting(User::getLogin).isEqualTo(userDto.getLogin());
        assertThat(user).extracting(User::getBirthdayDate).isEqualTo(userDto.getBirthdayDate());
        assertThat(user).extracting(User::getGender).isEqualTo(userDto.getGender());
    }
}
