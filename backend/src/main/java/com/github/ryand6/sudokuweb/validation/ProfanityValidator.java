package com.github.ryand6.sudokuweb.validation;

import com.github.ryand6.sudokuweb.config.ProfanityDictionaryLoader;
import com.github.ryand6.sudokuweb.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProfanityValidator implements ConstraintValidator<NoProfanity, String> {

    private final ProfanityDictionaryLoader profanityDictionaryLoader;

    public ProfanityValidator(ProfanityDictionaryLoader profanityDictionaryLoader) {
        this.profanityDictionaryLoader = profanityDictionaryLoader;
    }

    // Option to run validation without being used as part of an annotation
    public boolean isValid(String s) {
        return isValid(s, null);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {

        // Make sure string is ASCII chars only
        if (!StringUtils.isAsciiString(s)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Text contains non-ASCII characters").addConstraintViolation();
            return false;
        }

        // normalise text so that any leetspeak is substituted and then remove any other non-alphabetic chars
        s = StringUtils.normaliseString(s);
        s = StringUtils.removeNonAlphabeticChars(s);

        // Load bad word lists
        Set<String> wordOnlyBad = profanityDictionaryLoader.getWordOnlyBadWords();
        Set<String> alwaysBad = profanityDictionaryLoader.getAlwaysBadWords();

        // If string is a word that is bad by itself, fail validation
        for (String wordOnlyBadWord : wordOnlyBad) {
            if (s.equals(wordOnlyBadWord)) {
                if (context != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                                    "Text is a prohibited word")
                            .addConstraintViolation();
                }
                return false;
            }
        }

        // if string contains a word that is bad regardless of context, fail validation
        for (String alwaysBadWord: alwaysBad) {
            if (s.contains(alwaysBadWord)) {
                if (context != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                                    "Text contains prohibited content")
                            .addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
