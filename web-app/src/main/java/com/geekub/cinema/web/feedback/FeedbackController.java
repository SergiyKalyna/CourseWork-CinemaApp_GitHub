package com.geekub.cinema.web.feedback;

import com.geekhub.exception.UserHaveNotRightsException;
import com.geekhub.feedback.Feedback;
import com.geekhub.feedback.FeedbackConverter;
import com.geekhub.feedback.FeedbackService;
import com.geekhub.feedback.dto.FeedbackCreationDto;
import com.geekhub.models.Role;
import com.geekhub.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    private final FeedbackService feedbackService;
    private final FeedbackConverter feedbackConverter;

    public FeedbackController(FeedbackService feedbackService, FeedbackConverter feedbackConverter) {
        this.feedbackService = feedbackService;
        this.feedbackConverter = feedbackConverter;
    }

    @PostMapping("/{movieId}")
    @PreAuthorize("hasRole('USER')")
    public String addFeedback(@PathVariable("movieId") int movieId,
                              @ModelAttribute("newFeedback") FeedbackCreationDto feedbackCreationDto,
                              @AuthenticationPrincipal User user) {
        feedbackCreationDto.setMovieId(movieId);
        feedbackCreationDto.setUserId(user.getId());

        feedbackService.create(feedbackConverter.convertFromDto(feedbackCreationDto));

        return "redirect:/movies/" + movieId;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFeedback(@PathVariable("id") Long id) {
        int movieId = feedbackService.getFeedback(id).getMovieId();
        feedbackService.delete(id);

        return "redirect:/movies/" + movieId;
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('USER')")
    public String editFeedback(@PathVariable("id") Long id,
                               @AuthenticationPrincipal User user,
                               Model model) {
        Feedback feedbackFromDb = feedbackService.getFeedback(id);
        FeedbackCreationDto feedbackDto = feedbackConverter.convertToFeedbackCreationDto(feedbackFromDb);

        if (user.getRole() == Role.ADMIN || user.getId().equals(feedbackFromDb.getUserId())) {
            model.addAttribute("feedback", feedbackDto);
            logger.info("Started operation for edit feedback with id - " + id);
        } else {
            throw new UserHaveNotRightsException("You have not a rights to edit this feedback");
        }
        return "feedback/edit";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('USER')")
    public String updateFeedback(@PathVariable("id") Long id,
                                 @RequestParam("feedback") String feedback,
                                 @RequestParam("stars") String rating) {
        FeedbackCreationDto dto =
                feedbackConverter.convertToFeedbackCreationDto(feedbackService.getFeedback(id));
        dto.setMovieScore(Integer.parseInt(rating));
        dto.setFeedback(feedback);

        feedbackService.update(id, feedbackConverter.convertFromDto(dto));

        return "redirect:/movies/" + dto.getMovieId();
    }
}
