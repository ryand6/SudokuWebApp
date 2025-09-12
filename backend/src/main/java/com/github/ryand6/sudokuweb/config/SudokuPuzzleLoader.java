package com.github.ryand6.sudokuweb.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class SudokuPuzzleLoader {

    private final List<String> easyPuzzles = new ArrayList<>();
    private final List<String> mediumPuzzles = new ArrayList<>();
    private final List<String> hardPuzzles = new ArrayList<>();
    private final List<String> extremePuzzles = new ArrayList<>();

    // Load all puzzles at all difficulty levels when app starts
    @PostConstruct
    public void loadPuzzleLists() throws IOException {
        loadSudokuPuzzleFile("easy", easyPuzzles);
        loadSudokuPuzzleFile("medium", mediumPuzzles);
        loadSudokuPuzzleFile("hard", hardPuzzles);
        loadSudokuPuzzleFile("extreme", extremePuzzles);
    }

    // Load all sudoku puzzles from file for a given difficulty
    private void loadSudokuPuzzleFile(String difficulty, List<String> list) {
        String location = "data/problemsets/" + difficulty + "sudoku.tsv";
        Resource resource = new ClassPathResource(location);
        // Load problems within file into an array
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.isEmpty()) {
            throw new IllegalStateException("No Sudoku problems loaded.");
        }
    }

}
