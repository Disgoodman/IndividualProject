package com.example.models.controller;

import com.example.models.entity.Engine;
import com.example.models.entity.Image;
import com.example.models.entity.Motorcycle;
import com.example.models.repo.EngineRepository;
import com.example.models.repo.ImageRepository;
import com.example.models.repo.MotorcycleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/motorcycles")
@PreAuthorize("hasAnyAuthority('Technic')")
public class MotorcycleController {
    private final MotorcycleRepository repository;
    private final EngineRepository engineRepository;
    private final ImageRepository imageRepository;

    public MotorcycleController(MotorcycleRepository repository, EngineRepository engineRepository, ImageRepository imageRepository) {
        this.repository = repository;
        this.engineRepository = engineRepository;
        this.imageRepository = imageRepository;
    }

    @GetMapping
    public String index() {
        return "redirect:/motorcycles/all";
    }

    @GetMapping("/all")
    public String openAllBikesTable(Model model) {
        List<Motorcycle> all = repository.findAll();
        model.addAttribute("bikes", all);
        return "motorcycles/all_bikes";
    }

    @GetMapping("/search")
    public String searchBikes(
            @RequestParam(name = "s") String searchString,
            Model model
    ) {
        List<Motorcycle> filtered = repository.findByModelContainsIgnoreCase(searchString);
        model.addAttribute("bikes", filtered);
        return "motorcycles/all_bikes";
    }

    @GetMapping("{id}")
    public String openMotorcycle(@PathVariable int id) {
        return "redirect:/motorcycles/details/%d".formatted(id);
    }

    @GetMapping("/details/{id}")
    public String openDetails(
            @PathVariable int id,
            Model model
    ) {
        Optional<Motorcycle> found = repository.findById(id);
        if (found.isEmpty()) {
            return "redirect:/motorcycles/all";
        }

        Motorcycle motorcycle = found.get();
        model.addAttribute("motorcycle", motorcycle);
        AssignMotoImages(model, motorcycle);
        return "motorcycles/motorcycle_details";
    }

    static void AssignMotoImages(Model model, Motorcycle motorcycle) {
        List<Image> images = motorcycle.getImages();
        Base64.Encoder encoder = Base64.getEncoder();
        List<String> imageBytes = new ArrayList<>();
        images.forEach(image -> imageBytes.add(encoder.encodeToString(image.getData())));
        model.addAttribute("images", imageBytes);
    }

    @GetMapping("/add")
    public String openAddMotorcycle(
            @ModelAttribute Motorcycle motorcycle,
            Model model) {
        List<Engine> allEngines = engineRepository.findAll();
        model.addAttribute("engines", allEngines);
        return "motorcycles/add_new_bike";
    }

    @PostMapping("/add")
    public String addMotorcycle(
            @ModelAttribute @Valid Motorcycle motorcycle,
            BindingResult validationState,
            @RequestParam("files") MultipartFile[] files,
            Model model
    ) {
        if (validationState.hasErrors()) {
            List<Engine> allEngines = engineRepository.findAll();
            model.addAttribute("engines", allEngines);
            return "motorcycles/add_new_bike";
        }
        Motorcycle saved = repository.save(motorcycle);

        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            try {
                byte[] bytes = file.getInputStream().readAllBytes();
                images.add(new Image(bytes, originalFilename, saved));
            } catch (IOException e) {
                System.out.println("Не удалось обработать файл: " + originalFilename);
            }
        }
        if (!images.isEmpty()) {
            imageRepository.saveAll(images);
        }
        return "redirect:/motorcycles/all";
    }

    @GetMapping("/details/{id}/edit")
    public String openEditDetails(
            @PathVariable int id,
            @ModelAttribute(name = "details") Motorcycle motorcycle,
            Model model
    ) {
        Optional<Motorcycle> found = repository.findById(id);
        if (found.isEmpty()) {
            return "redirect:/motorcycles/all";
        }
        motorcycle = found.get();
        model.addAttribute("details", motorcycle);
        List<Engine> allEngines = engineRepository.findAll();
        model.addAttribute("engines", allEngines);
        return "motorcycles/edit_details";
    }

    @PostMapping("/details/{id}/edit")
    public String saveChangedDetails(
            @PathVariable int id,
            @ModelAttribute(name = "details") @Valid Motorcycle motorcycle,
            BindingResult validationState,
            Model model
    ) {
        Optional<Motorcycle> found = repository.findById(id);
        if (found.isEmpty()) {
            return "redirect:/motorcycles/all";
        }

        if (validationState.hasErrors()) {
            List<Engine> allEngines = engineRepository.findAll();
            model.addAttribute("engines", allEngines);
            return "motorcycles/edit_details";
        }

        repository.save(motorcycle);
        return String.format("redirect:/motorcycles/details/%d", motorcycle.getId());
    }

    @GetMapping("/{id}/delete")
    public String deleteMotorcycle(@PathVariable int id) {
        return "redirect:/motorcycles/details/%d/delete".formatted(id);
    }

    @GetMapping("/details/{id}/delete")
    public String deleteMotorcycleDetails(@PathVariable int id) {
        Optional<Motorcycle> found = repository.findById(id);
        if (found.isPresent()) {
            Motorcycle deleting = found.get();
            repository.delete(deleting);
        }
        return "redirect:/motorcycles/all";
    }
}
