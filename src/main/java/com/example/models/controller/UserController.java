package com.example.models.controller;

import com.example.models.entity.Profile;
import com.example.models.entity.Role;
import com.example.models.entity.User;
import com.example.models.repo.ProfileRepository;
import com.example.models.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasAnyAuthority('Administrator')")
public class UserController {

    //region ctor
    private final UserService service;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserService service,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder) {
        this.service = service;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //endregion

    //region table
    @GetMapping
    public String index() {
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String openAllUsersTable(Model model) {
        List<User> all = service.findAll();
        model.addAttribute("users", all);
        return "users/all_users";
    }

    @GetMapping("/search")
    public String searchUsers(
            @RequestParam(name = "s") String searchString,
            Model model
    ) {
        List<User> filtered = service.findAll(searchString);
        model.addAttribute("users", filtered);
        return "users/all_users";
    }
    //endregion

    //region add user
    @GetMapping("/add")
    public String openAddUserPage(
            @ModelAttribute User user,
            Model model
    ) {
        Role[] roles = Role.values();
        model.addAttribute("roles", roles);
        return "users/add_new_user";
    }

    @PostMapping("/add")
    public String addUser(
            Model model,
            @Valid User user,
            BindingResult validationState,
            @RequestParam Role role
    ) {
        user.setRoles(Collections.singleton(role));
        if (validationState.hasErrors()) {
            Role[] roles = Role.values();
            model.addAttribute("roles", roles);
            return "users/add_new_user";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        service.add(user);
        return "redirect:/users/all";
    }
    //endregion

    //region details
    @GetMapping("/{login}/details")
    @PreAuthorize("hasAnyAuthority('UnverifiedUser', 'Administrator', 'Technic', 'Seller')")
    public String openDetails(
            @PathVariable
            String login,
            @ModelAttribute("details") User details,
            Model model
    ) {
        details = service.find(login);
        model.addAttribute("details", details);

        return "users/details";
    }

    @GetMapping("/{login}/details/edit")
    public String openEditDetails(
            @PathVariable String login,
            Model model,
            @ModelAttribute("details") User details
    ) {
        details = service.find(login);
        model.addAttribute("details", details);

        Role[] roles = Role.values();
        model.addAttribute("available_roles", roles);
        return "users/edit_details";
    }

    @PostMapping("/{login}/details/edit")
    public String editDetails(
            @PathVariable String login,
            Model model,
            @ModelAttribute("details") User details,
            BindingResult validationState,
            @RequestParam Role role
    ) {
        service.find(login);
        details.setRoles(Collections.singleton(role));
        if (validationState.hasErrors()) {
            Role[] roles = Role.values();
            model.addAttribute("available_roles", roles);
            return "users/edit_details";
        }
        service.edit(details);
        return "redirect:/users/all";
    }
    //endregion

    //region profile
    @GetMapping("/{login}/profile/create")
    public String openCreateProfile(
            @PathVariable String login,
            Model model
    ) {
        User user = service.find(login);
        if (user.hasProfile()) {
            return "redirect:/users/%s/profile".formatted(login);
        }

        Profile profile = new Profile();
        model.addAttribute(profile);
        return "users/create_profile";
    }

    @PostMapping("/{login}/profile/create")
    public String createProfile(
            @PathVariable String login,
            @ModelAttribute @Valid Profile profile,
            BindingResult validationState
    ) {
        User user = service.find(login);

        if (validationState.hasErrors()) {
            return "users/create_profile";
        }
        profile.setUser(user);
        profileRepository.save(profile);
        return "redirect:/users/%s/profile".formatted(login);
    }

    @PreAuthorize("hasAnyAuthority('UnverifiedUser', 'Administrator', 'Technic', 'Seller')")
    @GetMapping("/{login}/profile")
    public String openProfile(
            @PathVariable String login,
            Model model
    ) {
        User user = service.find(login);

        Profile profile = user.getProfile();
        if (profile == null) {
            return "redirect:/users/all";
        }
        model.addAttribute(profile);

        return "users/profile";
    }

    @GetMapping("/{login}/profile/edit")
    public String openEditProfile(
            @PathVariable String login,
            Model model
    ) {
        User user = service.find(login);

        Profile profile = user.getProfile();
        if (profile == null) {
            return "redirect:/users/all";
        }
        model.addAttribute(profile);

        return "users/edit_profile";
    }

    @PostMapping("/{login}/profile/edit")
    public String saveEditProfile(
            @PathVariable String login,
            @ModelAttribute @Valid Profile profile,
            BindingResult validationState
    ) {
        service.find(login);
        if (validationState.hasErrors()) {
            return "users/edit_profile";
        }

        profileRepository.save(profile);
        return "redirect:/users/%s/profile".formatted(login);
    }
    //endregion

    //region delete user
    @GetMapping("/{login}/delete")
    public String deleteUser(
            @PathVariable String login
    ) {
        service.delete(login);
        return "redirect:/users/all";
    }
    //endregion
}