package com.geekhub.user.dto;

import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserCreateDto {

    private String login;
    private String password;
    private String firstName;
    private String secondName;
    private Gender gender;
    private LocalDate birthdayDate;
}
