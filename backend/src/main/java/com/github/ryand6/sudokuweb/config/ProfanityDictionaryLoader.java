package com.github.ryand6.sudokuweb.config;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Getter(AccessLevel.NONE)
    @Value("${profanity.always-bad:}")
    private String alwaysBadConfig;

    @Getter(AccessLevel.NONE)
    @Value("${profanity.word-only-bad:}")
    private String wordOnlyBadConfig;

    // Load all words from profanity lists during app startup
    @PostConstruct
    public void loadWordLists() throws IOException {
        loadWords("always-bad.txt", alwaysBadConfig, alwaysBadWords);
        loadWords("word-only-bad.txt", wordOnlyBadConfig, wordOnlyBadWords);
    }

    private void loadWords(String file, String configValue, Set<String> set) {
        if (configValue != null && !configValue.isBlank()) {
            for (String word : configValue.split(",")) {
                if (!word.isBlank()) {
                    set.add(word.toLowerCase().strip());
                }
            }
            log.info("Loaded {} words from configuration for {}", set.size(), file);
            return;
        }
        loadProfanityFile(file, set);
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
