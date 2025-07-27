package com.github.ryand6.sudokuweb.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DataSourceChecker implements CommandLineRunner {

    private final DataSource dataSource;

    public DataSourceChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Checking DataSource on startup: " + dataSource);

        // Example query to check DB connection
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                System.out.println("Database connection is valid.");
            } else {
                System.err.println("Database connection is NOT valid!");
            }
        } catch (SQLException e) {
            System.err.println("Database connection check failed: " + e.getMessage());
        }
    }
}
