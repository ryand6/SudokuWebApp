package com.github.ryand6.sudokuweb.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
/*
Handles exceptions and through HTTP Status and Messaging
 */
public class GlobalExceptionHandler {

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
