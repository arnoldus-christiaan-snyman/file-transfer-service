package com.github.acs.file.email.internal;

import com.github.acs.file.email.EmailRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Validator for the {@link EmailRequest} class to enforce custom validation rules.
 * This class ensures that the email request adheres to the following constraints:
 * <ul>
 *     <li>At least one valid 'to' recipient is required.</li>
 *     <li>The 'subject' field must not be empty.</li>
 *     <li>Only one of 'body' or 'templateName' can be set, but not both.</li>
 *     <li>If 'body' is set, 'templateVariables' must be empty.</li>
 *     <li>At least a 'body', 'templateName', or 'templateVariables' must be set.</li>
 *     <li>All email addresses in 'to', 'cc', and 'bcc' must be valid if provided.</li>
 * </ul>
 */
public class EmailRequestValidator implements ConstraintValidator<ValidEmailRequest, EmailRequest> {

    /**
     * Regular expression pattern for validating email addresses.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    /**
     * A set to store validation violation messages.
     */
    private final SequencedSet<String> violations;

    /**
     * Constructs a new {@code EmailRequestValidator} instance.
     */
    public EmailRequestValidator() {
        this.violations = new LinkedHashSet<>();
    }

    /**
     * Validates the given {@link EmailRequest} object against the defined constraints.
     *
     * @param emailRequest the email request to validate
     * @param context the constraint validator context
     * @return {@code true} if the email request is valid, {@code false} otherwise
     */
    @Override
    public boolean isValid(EmailRequest emailRequest, ConstraintValidatorContext context) {
        this.violations.clear();
        validateEmailRecipients(emailRequest);
        validateEmailSubject(emailRequest);
        validateBodyAndTemplateConstraints(emailRequest);

        return evaluateViolations(context);
    }

    /**
     * Validates the email recipient fields ('to', 'cc', 'bcc') in the email request.
     *
     * @param emailRequest the email request to validate
     */
    private void validateEmailRecipients(EmailRequest emailRequest) {
        final String invalidEmailAddressMessage = "The '%s' recipient field has one or more invalid email(s)";
        if(!isFieldValid(emailRequest.getTo())) {
            this.addViolationMessage("At least one valid 'to' recipient is required");
        }else if(isInvalidEmail(emailRequest.getTo(), true)) {
            this.addViolationMessage(invalidEmailAddressMessage, "to");
        }

        if(isInvalidEmail(emailRequest.getCc(), false)) {
            this.addViolationMessage(invalidEmailAddressMessage, "cc");
        }

        if(isInvalidEmail(emailRequest.getBcc(), false)) {
            this.addViolationMessage(invalidEmailAddressMessage, "bcc");
        }
    }

    /**
     * Validates the 'subject' field in the email request.
     *
     * @param emailRequest the email request to validate
     */
    private void validateEmailSubject(EmailRequest emailRequest) {
        if(!isFieldValid(emailRequest.getSubject())) {
            this.addViolationMessage("The 'subject' field is required");
        }
    }

    /**
     * Validates the constraints between 'body', 'templateName', and 'templateVariables' in the email request.
     *
     * @param emailRequest the email request to validate
     */
    private void validateBodyAndTemplateConstraints(EmailRequest emailRequest) {
        boolean hasBody = isFieldValid(emailRequest.getBody());
        boolean hasTemplateName = isFieldValid(emailRequest.getTemplateName());
        boolean hasTemplateVariables = isFieldValid(emailRequest.getTemplateVariables());

        if (!(hasBody || (hasTemplateName || hasTemplateVariables))) {
            this.addViolationMessage("At least a 'body' or 'templateName' or 'templateVariables' must be set");
        } else if (hasBody && hasTemplateName) {
            this.addViolationMessage("Only the 'body' or 'templateName' can be set, not both");
        } else if (hasBody && hasTemplateVariables) {
            this.addViolationMessage("If 'body' is set, 'templateVariables' must be empty");
        }
    }

    /**
     * Checks if the given collection of email addresses contains invalid emails.
     *
     * @param emailAddressCollection the collection of email addresses to validate
     * @param required whether the field is required
     * @return {@code true} if the collection contains invalid emails, {@code false} otherwise
     */
    public boolean isInvalidEmail(Collection<String> emailAddressCollection, boolean required) {
        if (!isFieldValid(emailAddressCollection)) {
            return required; // Valid if not required
        }
        return emailAddressCollection.stream().noneMatch(emailAddress -> EMAIL_PATTERN.matcher(emailAddress).matches());
    }

    /**
     * Checks if the given collection is valid (not null and not empty).
     *
     * @param collection the collection to validate
     * @return {@code true} if the collection is valid, {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isFieldValid(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Checks if the given map is valid (not null and not empty).
     *
     * @param collection the map to validate
     * @return {@code true} if the map is valid, {@code false} otherwise
     */
    private boolean isFieldValid(Map<?, ?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Checks if the given string is valid (not null and not blank).
     *
     * @param value the string to validate
     * @return {@code true} if the string is valid, {@code false} otherwise
     */
    private boolean isFieldValid(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Adds a violation message to the list of violations.
     *
     * @param message the violation message to add
     */
    private void addViolationMessage(String message) {
        this.violations.add(message);
    }

    /**
     * Adds a formatted violation message to the list of violations.
     *
     * @param template the message template
     * @param args the arguments to format the template
     */
    @SuppressWarnings("SameParameterValue")
    private void addViolationMessage(String template, Object... args) {
        this.addViolationMessage(String.format(template, args));
    }

    /**
     * Evaluates the collected violations and updates the validation context.
     *
     * @param context the constraint validator context
     * @return {@code true} if there are no violations, {@code false} otherwise
     */
    private boolean evaluateViolations(ConstraintValidatorContext context) {
        boolean hasNoViolations = violations.isEmpty();
        if(!hasNoViolations) {
            context.disableDefaultConstraintViolation();
            for(String message : violations) {
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
        }
        return hasNoViolations;
    }
}
