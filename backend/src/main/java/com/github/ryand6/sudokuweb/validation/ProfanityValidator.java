package com.github.ryand6.sudokuweb.validation;

import com.github.ryand6.sudokuweb.config.ProfanityDictionaryLoader;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ProfanityValidator implements ConstraintValidator<NoProfanity, String> {

    private final ProfanityDictionaryLoader profanityDictionaryLoader;

    public ProfanityValidator(ProfanityDictionaryLoader profanityDictionaryLoader) {
        this.profanityDictionaryLoader = profanityDictionaryLoader;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
