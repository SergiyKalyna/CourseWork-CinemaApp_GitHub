package com.geekub.cinema.web.hall;

import com.geekhub.cinemahall.CinemaHall;
import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/halls")
public class HallController {
    private static final Logger logger = LoggerFactory.getLogger(HallController.class);

    private final CinemaHallService cinemaHallService;

    public HallController(CinemaHallService cinemaHallService) {
        this.cinemaHallService = cinemaHallService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public String showHalls(Model model) {
        model.addAttribute("halls", cinemaHallService.getAllHalls());

        return "hall/all-halls";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editHall(Model model, @PathVariable("id") int id) {
        model.addAttribute("cinemaHall", cinemaHallService.getHall(id));
        logger.info("Started operation to edit hall with id -" + id);

        return "hall/edit";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateHall(@ModelAttribute("cinemaHall") CinemaHall cinemaHall,
                             @PathVariable("id") int id) {
        cinemaHallService.updateHall(id, cinemaHall);

        return "redirect:/halls";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createHall(@ModelAttribute("cinemaHall") CinemaHall cinemaHall) {
        logger.info("Started operation to create hall");

        return "hall/create";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addHall(@ModelAttribute("cinemaHall") CinemaHall cinemaHall) {
        cinemaHallService.addHall(cinemaHall);

        return "redirect:/halls";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteHall(@PathVariable("id") int id) {
        cinemaHallService.deleteHall(id);

        return "redirect:/halls";
    }
}
