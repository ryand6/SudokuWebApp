package com.github.ryand6.sudokuweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/user-not-found")
    public String userNotFoundPage() {
        return "error/user-not-found"; // Render view "templates/error/user-not-found.html"
    }

}
