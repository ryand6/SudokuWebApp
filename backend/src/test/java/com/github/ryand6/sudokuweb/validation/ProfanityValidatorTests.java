package com.github.ryand6.sudokuweb.validation;

import com.github.ryand6.sudokuweb.config.ProfanityDictionaryLoader;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfanityValidatorTests {

    private ProfanityValidator validator;

    private Set<String> badOnlyWords;
    private Set<String> alwaysBadWords;
    private ConstraintValidatorContext context;


    @BeforeEach
    void setup() throws IOException {
        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        // When buildConstraintViolationWithTemplate is called, return the mocked builder
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);

        // When addConstraintViolation is called, return itself (fluent API)
        when(builder.addConstraintViolation()).thenReturn(context);

        // Create a fake loader for testing
        ProfanityDictionaryLoader loader = new ProfanityDictionaryLoader();
        loader.loadWordLists();

        badOnlyWords = loader.getWordOnlyBadWords();
        alwaysBadWords = loader.getAlwaysBadWords();
        validator = new ProfanityValidator(loader);
    }

    @Test
    void testValidString() {
        assertTrue(validator.isValid("hello", context));
        assertTrue(validator.isValid("goodword", context));
    }

    @Test
    void testWordOnlyBadExactMatch() {
        assertFalse(validator.isValid("ass", context));
        assertFalse(validator.isValid("hoe", context));
    }

    @Test
    void testWordOnlyBadCaseInsensitive() {
        assertFalse(validator.isValid("CoCk", context));
    }

    @Test
    void testAlwaysBadSubstring() {
        assertFalse(validator.isValid("you are a fucker!", context));
        assertFalse(validator.isValid("amerikkka", context));
    }

    @Test
    void testLeetspeak() {
        assertFalse(validator.isValid("h03", context));
        assertFalse(validator.isValid("t1t", context));
        assertFalse(validator.isValid(" w4nk3rs", context));
        assertFalse(validator.isValid("80n3r5", context));
    }

    @Test
    void testNonAlphabeticCharsStripped() {
        assertFalse(validator.isValid("cu-ck", context));
        assertFalse(validator.isValid("f_u_c_k", context));
    }

    @Test
    void testAsciiOnlyCheck() {
        assertFalse(validator.isValid("café", context)); // é > 127, fails ASCII check
        assertTrue(validator.isValid("hello", context));
    }

    @Test
    void testFalsePositivesAvoided() {
        assertTrue(validator.isValid("class", context)); // contains "ass" but as part of word
        assertTrue(validator.isValid("passage", context));
    }
}

