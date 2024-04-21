package com.github.ryand6.sudokuweb.services;

import org.springframework.stereotype.Service;

@Service
public class SudokuGeneratorService {

    public static int[][] generatePuzzle(String difficulty) {
        int[][] grid = new int[8][8];
        return grid;
    }

}
