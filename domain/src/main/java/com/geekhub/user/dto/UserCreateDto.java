package com.geekhub.user.dto;

import com.geekhub.models.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    private String login;
    private String password;
    private String firstName;
    private String secondName;
    private Gender gender;
    private LocalDate birthdayDate;
}
