package com.geekub.cinema.web.feedback;

import com.geekhub.exception.UserHaveNotRightsException;
import com.geekhub.feedback.Feedback;
import com.geekhub.feedback.FeedbackService;
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

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/{movieId}")
    @PreAuthorize("hasRole('USER')")
    public String addFeedback(@PathVariable("movieId") int movieId,
                              @ModelAttribute("newFeedback") Feedback feedback,
                              @AuthenticationPrincipal User user) {
        feedback.setMovieId(movieId);
        feedback.setUserId(user.getId());
        feedback.setUserName(user.getFirstName() + " " + user.getSecondName());

        feedbackService.create(feedback);

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
        if (user.getRole() == Role.ADMIN || user.getId().equals(feedbackFromDb.getUserId())) {
            model.addAttribute("feedback", feedbackFromDb);
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
        Feedback feedbackFromDb = feedbackService.getFeedback(id);
        feedbackFromDb.setMovieScore(Integer.parseInt(rating));
        feedbackFromDb.setFeedback(feedback);

        feedbackService.update(id, feedbackFromDb);

        return "redirect:/movies/" + feedbackFromDb.getMovieId();
    }
}
