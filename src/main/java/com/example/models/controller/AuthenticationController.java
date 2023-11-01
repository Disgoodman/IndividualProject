package com.example.models.controller;

import com.example.models.entity.Role;
import com.example.models.entity.User;
import com.example.models.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;

@Controller
public class AuthenticationController {

    private final PasswordEncoder passwordEncoder;
    private final UserService service;

    public AuthenticationController(PasswordEncoder passwordEncoder, UserService service) {
        this.passwordEncoder = passwordEncoder;
        this.service = service;
    }

    @GetMapping("/login")
    public String openLogin(@ModelAttribute User user) {
        return "auth/login";
    }

    @GetMapping("/register")
    public String openRegistration(@ModelAttribute User user) {
        return "auth/registration";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute @Valid User user,
            BindingResult validationState,
            Model model,
            HttpServletRequest request
    ) throws ServletException {
        if (validationState.hasErrors()) {
            return "auth/registration";
        }

        if (service.existWithLogin(user.getLogin())) {
            model.addAttribute("message", "User with such login already exists!");
            return "auth/registration";
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User toRegistration = new User(user.getLogin(), encodedPassword);
        toRegistration.setRoles(Collections.singleton(Role.UnverifiedUser));
        service.add(toRegistration);

        request.login(user.getLogin(), user.getPassword());
        return "redirect:/home";
    }
}