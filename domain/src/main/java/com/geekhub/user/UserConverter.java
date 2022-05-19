package com.geekhub.user;

import com.geekhub.user.dto.UserCreateDto;
import com.geekhub.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {

    public UserDto convertToDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setRole(user.getRole());
        userDto.setLogin(user.getLogin());
        userDto.setFirstName(user.getFirstName());
        userDto.setSecondName(user.getSecondName());
        userDto.setGender(user.getGender());
        userDto.setBirthdayDate(user.getBirthdayDate());

        return userDto;
    }

    public List<UserDto> convertToListDto(List<User> users){
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public User convertFromUserCreateDto(UserCreateDto userCreateDto){
        User user = new User();
        user.setLogin(userCreateDto.getLogin());
        user.setPassword(userCreateDto.getPassword());
        user.setFirstName(userCreateDto.getFirstName());
        user.setSecondName(userCreateDto.getSecondName());

        return user;
    }

    public User convertFromUserDto(UserDto userDto){
        User user = new User();
        user.setId(userDto.getId());
        user.setRole(userDto.getRole());
        user.setLogin(userDto.getLogin());
        user.setFirstName(userDto.getFirstName());
        user.setSecondName(userDto.getSecondName());
        user.setGender(userDto.getGender());
        user.setBirthdayDate(userDto.getBirthdayDate());

        return user;
    }
}
