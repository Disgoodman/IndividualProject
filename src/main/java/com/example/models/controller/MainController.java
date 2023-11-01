package com.example.models.controller;

import com.example.models.entity.Role;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class MainController {
    @GetMapping
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String openHomePage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            Role role = (Role) authority;
            return switch (role) {
                case UnverifiedUser, Seller -> "redirect:/shop";
                case Administrator -> "redirect:/users";
                case Technic -> "redirect:/engines";
            };
        }
        throw new BadCredentialsException("User's roles are not set!");
    }
}
