package com.github.ryand6.sudokuweb.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Component
public class ProfanityDictionaryLoader {

    private final Set<String> alwaysBadWords = new HashSet<>();
    private final Set<String> wordOnlyBadWords = new HashSet<>();

    // Load all words from profanity lists during app startup
    @PostConstruct
    public void loadWordLists() throws IOException {
        loadProfanityFile("always-bad.txt", alwaysBadWords);
        loadProfanityFile("word-only-bad.txt", wordOnlyBadWords);
    }

    // Load words from a profanity list into a hashset
    private void loadProfanityFile(String file, Set<String> set) {
        String location = "data/profanity/" + file;
        ClassPathResource resource = new ClassPathResource(location);
        if (!resource.exists()) {
            log.warn("Profanity file not found: {}", location);
            return;
        }
        // Load profane words into hashset
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    set.add(line.toLowerCase().strip());
                }
            }
            log.info("Loaded {} words from {}", set.size(), location);
        } catch (IOException e) {
            log.error("Failed to load profanity file: {}", location, e);
        }
    }

}
