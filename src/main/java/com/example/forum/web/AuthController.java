package com.example.forum.web;

import com.example.forum.exception.DuplicateAccountException;
import com.example.forum.service.UserService;
import com.example.forum.web.form.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult binding) {
        if (binding.hasErrors()) {
            return "register";
        }

        try {
            userService.register(
                    form.getUsername(),
                    form.getEmail(),
                    form.getPassword(),
                    form.getDisplayName()
            );
        } catch (DuplicateAccountException exception) {
            binding.reject("duplicate", exception.getMessage());
            return "register";
        }

        return "redirect:/login?registered";
    }
}
