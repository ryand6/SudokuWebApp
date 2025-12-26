package com.github.ryand6.sudokuweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SudokuWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SudokuWebApplication.class, args);
	}

}
