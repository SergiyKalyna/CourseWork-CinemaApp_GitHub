package com.geekub.cinema.web.registration;

import com.geekhub.exception.ValidationException;
import com.geekhub.models.Gender;
import com.geekhub.user.UserConverter;
import com.geekhub.user.UserService;
import com.geekhub.user.dto.UserCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final UserService userService;
    private final UserConverter userConverter;

    public RegistrationController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @GetMapping
    public String doRegister(@ModelAttribute ("user") UserCreateDto user) {
        logger.info("User registration operation started");
        return "registration/registration";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam("login") String login,
                          @RequestParam("password") String password,
                          @RequestParam("confirmPassword") String confirmPassword,
                          @RequestParam("firstName") String firstName,
                          @RequestParam("secondName") String secondName,
                          @RequestParam("gender") String gender,
                          @RequestParam("birthdayDate") String birthdayDate) {

        if (!password.equals(confirmPassword)){
            throw new ValidationException("New password and confirmation password do not match");
        }

        UserCreateDto user = new UserCreateDto();
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setSecondName(secondName);
        user.setGender(Gender.valueOf(gender.toUpperCase(Locale.ROOT)));
        user.setBirthdayDate(LocalDate.parse(birthdayDate));

        userService.saveUser(userConverter.convertFromUserCreateDto(user));

        return "redirect:/menu";
    }
}


