package com.geekub.cinema.web.registration;

import com.geekhub.exception.ValidationException;
import com.geekhub.models.Gender;
import com.geekhub.user.User;
import com.geekhub.user.UserService;
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

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public String doRegister(@ModelAttribute ("user") User user) {
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

        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setSecondName(secondName);
        user.setGender(Gender.valueOf(gender.toUpperCase(Locale.ROOT)));
        user.setBirthdayDate(LocalDate.parse(birthdayDate));

        userService.saveUser(user);

        return "redirect:/menu";
    }
}


