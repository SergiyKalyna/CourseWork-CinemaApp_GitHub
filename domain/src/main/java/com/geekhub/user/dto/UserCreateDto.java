package com.geekhub.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotEmpty(message = "Login should be not empty")
    @NotBlank(message = "Login should be not blank")
    @Size(min = 3, max = 25, message = "Login should be have min 3 chars, max 25 chars")
    private String login;

    @NotEmpty(message = "Password should be not empty")
    @NotBlank(message = "Password should be not blank")
    @Size(min = 6, max = 30, message = "Password should be have min 6 chars, max 30 chars")
    private String password;

    @NotEmpty(message = "Confirm password should be not empty")
    @NotBlank(message = "Confirm password should be not blank")
    @Size(min = 6, max = 30, message = "Confirm password should be have min 6 chars, max 30 chars")
    private String confirmPassword;

    @NotEmpty(message = "First name should be not empty")
    @NotBlank(message = "First name should be not blank")
    @Size(min = 3, max = 30, message = "First name should be have min 3 chars, max 30 chars")
    private String firstName;

    @NotEmpty(message = "Second name should be not empty")
    @NotBlank(message = "Second name should be not blank")
    @Size(min = 3, max = 30, message = "Second name should be have min 3 chars, max 30 chars")
    private String secondName;
}
