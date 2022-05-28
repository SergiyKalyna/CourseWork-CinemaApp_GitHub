package com.geekub.cinema.web.hall;

import com.geekhub.cinemahall.CinemaHall;
import com.geekhub.cinemahall.CinemaHallConverter;
import com.geekhub.cinemahall.dto.CinemaHallDto;
import com.geekhub.cinemahall.CinemaHallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/halls")
public class HallController {
    private static final Logger logger = LoggerFactory.getLogger(HallController.class);

    private final CinemaHallService cinemaHallService;
    private final CinemaHallConverter cinemaHallConverter;

    public HallController(CinemaHallService cinemaHallService, CinemaHallConverter cinemaHallConverter) {
        this.cinemaHallService = cinemaHallService;
        this.cinemaHallConverter = cinemaHallConverter;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public String showHalls(Model model) {
        List<CinemaHall> cinemaHalls = cinemaHallService.getAllHalls();
        List<CinemaHallDto> halls = cinemaHallConverter.convertListToDto(cinemaHalls);

        model.addAttribute("halls", halls);

        return "hall/all-halls";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editHall(Model model, @PathVariable("id") int id) {
        CinemaHallDto cinemaHall = cinemaHallConverter.convertToDto(cinemaHallService.getHall(id));

        model.addAttribute("cinemaHall", cinemaHall);
        logger.info("Started operation to edit hall with id -" + id);

        return "hall/edit";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateHall(@ModelAttribute("cinemaHall") CinemaHallDto cinemaHall,
                             @PathVariable("id") int id) {
        CinemaHall hall = cinemaHallConverter.convertFromDto(cinemaHall);

        cinemaHallService.updateHall(id, hall);

        return "redirect:/halls";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createHall() {
        logger.info("Started operation to create hall");

        return "hall/create";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addHall(@ModelAttribute("cinemaHall") CinemaHallDto cinemaHall) {
        cinemaHallService.addHall(cinemaHallConverter.convertFromDto(cinemaHall));

        return "redirect:/halls";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteHall(@PathVariable("id") int id) {
        cinemaHallService.deleteHall(id);

        return "redirect:/halls";
    }
}
