package com.github.ryand6.sudokuweb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@SpringBootApplication
@RestController
public class SudokuWebApplication implements CommandLineRunner {

	private final DataSource dataSource;

	public SudokuWebApplication(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static void main(String[] args) {
		SpringApplication.run(SudokuWebApplication.class, args);
	}

	@Override
	public void run(String[] args) {
		System.out.println("Datasource: " + dataSource.toString());
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("SELECT 1 * 1");
	}

	@GetMapping("/")
	public String hello(@RequestParam(value = "name", defaultValue = "TodoSudoku") String name) {
		return String.format("Hello %s!", name);
	}
}
