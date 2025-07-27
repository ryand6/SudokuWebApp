package com.github.ryand6.sudokuweb.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
/*
Handles exceptions and through HTTP Status and Messaging - handles exceptions that propagate to Controller
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/user-not-found"; // renders error/user-not-found.html
    }

    @ExceptionHandler(InvalidDifficultyException.class)
    public ResponseEntity<String> handleInvalidDifficultyException(InvalidDifficultyException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TooManyActivePlayersException.class)
    public ResponseEntity<String> handleTooManyActivePlayersException(TooManyActivePlayersException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

}
