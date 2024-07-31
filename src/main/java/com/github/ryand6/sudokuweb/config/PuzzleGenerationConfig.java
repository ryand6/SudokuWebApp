package com.github.ryand6.sudokuweb.config;

import com.github.ryand6.sudokuweb.services.PuzzleGenerator;
import com.github.ryand6.sudokuweb.services.impl.PuzzleGeneratorFromFile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PuzzleGenerationConfig {

    @Bean
    public PuzzleGenerator puzzleGenerator() {
        return new PuzzleGeneratorFromFile();
    }

}
