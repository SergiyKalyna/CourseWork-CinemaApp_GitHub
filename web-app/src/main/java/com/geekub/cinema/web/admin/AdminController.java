package com.geekub.cinema.web.admin;

import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import com.geekhub.user.User;
import com.geekhub.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());

        return "admin/all-users";
    }

    @PostMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);

        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.findById(id));
        logger.info("Started operation to edit user with id -" + id);

        return "admin/edit";
    }

    @PostMapping("/users/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@RequestParam("role") String role,
                         @RequestParam("firstName") String firstName,
                         @RequestParam("secondName") String secondName,
                         @RequestParam("gender") String gender,
                         @RequestParam("birthdayDate") String birthdayDate,
                         @PathVariable("id") Long id) {

        User user = new User();
        user.setRole(Role.valueOf(role.toUpperCase(Locale.ROOT)));
        user.setFirstName(firstName);
        user.setSecondName(secondName);
        user.setGender(Gender.valueOf(gender.toUpperCase(Locale.ROOT)));
        user.setBirthdayDate(LocalDate.parse(birthdayDate));

        userService.updateUser(id, user);
        return "redirect:/admin/users";
    }
}
