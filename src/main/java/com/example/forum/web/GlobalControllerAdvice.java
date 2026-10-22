package com.example.forum.web;

import com.example.forum.exception.ResourceNotFoundException;
import com.example.forum.security.ForumUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public ForumUserDetails currentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof ForumUserDetails user) {
            return user;
        }
        return null;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error/404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException exception, Model model) {
        model.addAttribute("message", exception.getMessage());
        return "error/400";
    }
}
