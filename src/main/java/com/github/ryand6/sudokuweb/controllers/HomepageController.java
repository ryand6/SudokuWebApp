package com.github.ryand6.sudokuweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    // Renders homepage view
    @GetMapping("/")
    public String getHomepage() {
        return "homepage"; // Render homepage.html
    }

}
