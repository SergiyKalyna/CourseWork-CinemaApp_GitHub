package com.geekub.cinema.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;
import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@ControllerAdvice
@RequestMapping("/error")
public class ErrorHandlerControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerControllerAdvice.class);

    @ExceptionHandler(value = RuntimeException.class)
    public ModelAndView errorHandler(HttpServletRequest req, RuntimeException exception) {

        logger.error(exception.getMessage());
        ModelAndView mav = new ModelAndView("error");

        mav.addObject("message", exception.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("url", req.getRequestURL());
        mav.addObject("reason", req.getAttribute(ERROR_MESSAGE));
        mav.addObject("status", req.getAttribute(ERROR_STATUS_CODE));
        
        return mav;
    }
}
