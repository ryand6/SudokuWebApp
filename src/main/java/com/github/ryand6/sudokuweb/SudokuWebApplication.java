package com.github.ryand6.sudokuweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SudokuWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SudokuWebApplication.class, args);
	}
	@GetMapping("/")
	public String hello(@RequestParam(value = "name", defaultValue = "Sudoku Guru") String name) {
		return String.format("Hello %s!", name);
	}
}
