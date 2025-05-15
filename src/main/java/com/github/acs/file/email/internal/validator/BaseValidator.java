package com.github.acs.file.email.internal.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SequencedSet;

public abstract class BaseValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    protected static final String DEFAULT_REQUIRED_FIELD_MESSAGE = "The '%s' field is required";
    /**
     * A set to store validation violation messages.
     */
    private final SequencedSet<String> violations;

    protected BaseValidator() {
        this.violations = new LinkedHashSet<>();
    }

    protected abstract void validate(T validationEntity);

    @Override
    public final void initialize(A constraintAnnotation) {
        this.violations.clear();
    }

    @Override
    public final boolean isValid(T validationEntity, ConstraintValidatorContext context) {
        this.validate(validationEntity);
        return this.evaluateViolations(context);
    }

    /**
     * Adds a violation message to the list of violations.
     *
     * @param message the violation message to add
     */
    protected final void addViolationMessage(String message) {
        this.violations.add(message);
    }

    /**
     * Adds a formatted violation message to the list of violations.
     *
     * @param template the message template
     * @param args the arguments to format the template
     */
    @SuppressWarnings("SameParameterValue")
    protected final void addViolationMessage(String template, Object... args) {
        this.addViolationMessage(String.format(template, args));
    }

    protected final boolean hasViolations() {
        return !this.violations.isEmpty();
    }

    /**
     * Evaluates the collected violations and updates the validation context.
     *
     * @param context the constraint validator context
     * @return {@code true} if there are no violations, {@code false} otherwise
     */
    protected final boolean evaluateViolations(ConstraintValidatorContext context) {
        boolean hasNoViolations = !hasViolations();
        if(!hasNoViolations) {
            context.disableDefaultConstraintViolation();
            for(String message : violations) {
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
        }
        return hasNoViolations;
    }

    /**
     * Checks if the given collection is valid (not null and not empty).
     *
     * @param collection the collection to validate
     * @return {@code true} if the collection is valid, {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected final boolean isFieldValid(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Checks if the given collection is valid (not null and not empty).
     *
     * @param collection the collection to validate
     * @return {@code true} if the collection is valid, {@code false} otherwise
     */
    protected final boolean isFieldValid(Map<?, ?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Checks if the given string is valid (not null and not blank).
     *
     * @param value the string to validate
     * @return {@code true} if the string is valid, {@code false} otherwise
     */
    protected final boolean isFieldValid(String value) {
        return value != null && !value.isBlank();
    }

}
