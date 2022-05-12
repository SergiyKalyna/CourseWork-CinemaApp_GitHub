package com.geekub.cinema.web.menu;

import com.geekhub.models.Role;
import com.geekhub.movie.MovieService;
import com.geekhub.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainMenuController {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuController.class);
    private final MovieService movieService;

    public MainMenuController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public String startPage() {
        logger.info("Showed start page");

        return "index";
    }

    @GetMapping("/menu")
    @PreAuthorize("hasRole('USER')")
    public String showMenu(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("movies", movieService.showLast3Movies());
        logger.info("Showed main menu page");

        model.addAttribute("checkUserRights", user.getRole().equals(Role.ADMIN));

        return "menu/menu-page";
    }
}
