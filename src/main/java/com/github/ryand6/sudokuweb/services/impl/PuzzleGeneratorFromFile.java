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
public class PuzzleGeneratorFromFile implements PuzzleGenerator {

    public List<String> generatePuzzle(String difficulty) {
        List<String> puzzleDetails = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        String location = "data/problemsets/" + difficulty + "sudoku.tsv";
        Resource resource = new ClassPathResource(location);
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
        int index = new Random().nextInt(lines.size());
        String randomProblem = lines.get(index);
        String[] parts = randomProblem.split("\t");
        puzzleDetails.add(parts[0]);
        puzzleDetails.add(parts[1]);
        return puzzleDetails;
    }

}
