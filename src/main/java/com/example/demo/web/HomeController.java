package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    static class Category {
        String name;
        List<String> images;
        Category(String name, List<String> images) {
            this.name = name;
            this.images = images;
        }
        public String getName() { return name; }
        public List<String> getImages() { return images; }
    }

    @GetMapping("/")
    public String home(Model model) {
        // Using placeholder images from picsum.photos so you don't need local files.
        var nature = new Category("Nature", List.of(
                "https://picsum.photos/seed/n1/800/600",
                "https://picsum.photos/seed/n2/800/600",
                "https://picsum.photos/seed/n3/800/600",
                "https://picsum.photos/seed/n4/800/600",
                "https://picsum.photos/seed/n5/800/600"
        ));

        var cities = new Category("Cities", List.of(
                "https://picsum.photos/seed/c1/800/600",
                "https://picsum.photos/seed/c2/800/600",
                "https://picsum.photos/seed/c3/800/600",
                "https://picsum.photos/seed/c4/800/600"
        ));

        var animals = new Category("Animals", List.of(
                "https://picsum.photos/seed/a1/800/600",
                "https://picsum.photos/seed/a2/800/600",
                "https://picsum.photos/seed/a3/800/600",
                "https://picsum.photos/seed/a4/800/600"
        ));

        model.addAttribute("categories", List.of(nature, cities, animals));
        return "index"; // renders templates/index.html
    }
}
