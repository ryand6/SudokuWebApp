package com.github.ryand6.sudokuweb.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.web.csrf.CsrfToken;


// Make csrf token available to the frontend, including Xor encoded token if required
@RestController
public class CsrfController {

    @GetMapping("/csrf")
    public CsrfToken csrfToken(CsrfToken csrfToken) {
        return csrfToken;
    }

}
