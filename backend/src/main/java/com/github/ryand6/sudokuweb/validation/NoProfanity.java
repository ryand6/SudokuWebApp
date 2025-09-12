package com.github.ryand6.sudokuweb.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
    Custom validation annotation used to detect if profanity exists within text
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ProfanityValidator.class)
public @interface NoProfanity {
    String message() default "Must not contain profanity";
    // defaults used for annotation spec in case a different validator engine requires them, implementation not required
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
