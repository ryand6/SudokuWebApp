package com.github.ryand6.sudokuweb.services;

import java.util.List;

public interface PuzzleGenerator {

    public List<String> generatePuzzle(String difficulty);

}
