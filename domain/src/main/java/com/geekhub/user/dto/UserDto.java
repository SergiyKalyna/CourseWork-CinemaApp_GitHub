package com.geekhub.user.dto;

import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String login;
    private Role role;
    private String firstName;
    private String secondName;
    private Gender gender;
    private LocalDate birthdayDate;
}
