package com.geekub.cinema.web.registration;

import com.geekhub.models.Gender;
import com.geekhub.user.User;
import com.geekhub.user.UserConverter;
import com.geekhub.user.UserService;
import com.geekhub.user.dto.UserCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public String doRegister(@ModelAttribute("user") UserCreateDto user) {
        logger.info("User registration operation started");
        return "registration/registration";
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("user") UserCreateDto userDto,
                          BindingResult bindingResult,
                          @RequestParam("gender") String gender,
                          @RequestParam("birthdayDate") String birthdayDate) {

        userService.checkPassword(userDto.getPassword(), userDto.getConfirmPassword(), bindingResult);
        if (bindingResult.hasErrors()) {
            return "registration/registration";
        }

        User user = userConverter.convertFromUserCreateDto(userDto);
        user.setGender(Gender.valueOf(gender.toUpperCase(Locale.ROOT)));
        user.setBirthdayDate(LocalDate.parse(birthdayDate));

        userService.saveUser(user);

        return "redirect:/menu";
    }
}


