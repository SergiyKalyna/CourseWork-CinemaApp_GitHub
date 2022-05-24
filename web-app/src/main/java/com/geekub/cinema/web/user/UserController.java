package com.geekub.cinema.web.user;


import com.geekhub.exception.ValidationException;
import com.geekhub.models.Gender;
import com.geekhub.user.User;
import com.geekhub.user.UserConverter;
import com.geekhub.user.UserService;
import com.geekhub.user.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserConverter userConverter;

    public UserController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public String getProfile(@AuthenticationPrincipal User user, Model model) {
        UserDto profileUser = userConverter.convertToDto(userService.findById(user.getId()));

        model.addAttribute("user", profileUser);

        return "user/profile";
    }

    @GetMapping("/{id}/edit_password")
    @PreAuthorize("hasRole('USER')")
    public String editPassword(Model model, @PathVariable("id") Long id) {
        model.addAttribute("id", id);
        logger.info("Started operation for edit password for user with id -" + id);
        return "user/edit-password";
    }

    @PostMapping("/{id}/update_password")
    @PreAuthorize("hasRole('USER')")
    public String updatePassword(@RequestParam("oldPassword") String oldPassword,
                         @RequestParam("newPassword") String newPassword,
                         @RequestParam("confirmPassword") String confirmPassword,
                         @PathVariable("id") Long id) {
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("New password and confirmation password do not match");
        }

        userService.changePassword(id, oldPassword, newPassword);
        return "redirect:/user/profile";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('USER')")
    public String edit(Model model, @PathVariable("id") Long id) {
        UserDto userToEdit = userConverter.convertToDto(userService.findById(id));

        model.addAttribute("user", userToEdit);
        logger.info("Started operation for edit profile of user with id -" + id);

        return "user/edit";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('USER')")
    public String update(@RequestParam("firstName") String firstName,
                         @RequestParam("secondName") String secondName,
                         @RequestParam("gender") String gender,
                         @RequestParam("birthdayDate") String birthdayDate,
                         @PathVariable("id") Long id) {

        UserDto user = userConverter.convertToDto(userService.findById(id));
        user.setFirstName(firstName);
        user.setSecondName(secondName);
        user.setGender(Gender.valueOf(gender.toUpperCase(Locale.ROOT)));
        user.setBirthdayDate(LocalDate.parse(birthdayDate));

        userService.updateUser(id, userConverter.convertFromUserDto(user));
        return "redirect:/user/profile";
    }
}
