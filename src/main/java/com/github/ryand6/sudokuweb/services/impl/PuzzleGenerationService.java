package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.services.PuzzleGenerator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PuzzleGenerationService implements PuzzleGenerator {

    /* Randomly select problem and its solution from a file with the corresponding
    difficulty type. File name in format: <difficulty>sudoku.tsv, file in format:
    col1 = string representation of the problem as a nested int array, col2 = string
    representation of the solution to the problem as a nested int array */
    public List<String> generatePuzzle(String difficulty) {
        List<String> puzzleDetails = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        // Get list of problems with corresponding difficulty rating
        String location = "data/problemsets/" + difficulty + "sudoku.tsv";
        Resource resource = new ClassPathResource(location);
        // Load problems within file into an array
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("No Sudoku problems loaded.");
        }
        // Randomly select problem from array
        int index = new Random().nextInt(lines.size());
        String randomProblem = lines.get(index);
        String[] parts = randomProblem.split("\t");
        // First part is sudokuPuzzleEntity
        puzzleDetails.add(parts[0]);
        // Second part is solution
        puzzleDetails.add(parts[1]);
        return puzzleDetails;
    }

}
