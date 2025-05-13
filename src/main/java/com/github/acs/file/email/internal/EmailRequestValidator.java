package com.github.acs.file.email.internal;

import com.github.acs.file.email.EmailRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.*;
import java.util.regex.Pattern;

public class EmailRequestValidator implements ConstraintValidator<ValidEmailRequest, EmailRequest> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private final SequencedSet<String> violations;

    public EmailRequestValidator() {
        this.violations = new LinkedHashSet<>();
    }

    @Override
    public boolean isValid(EmailRequest emailRequest, ConstraintValidatorContext context) {
        this.violations.clear();
        validateEmailRecipients(emailRequest);
        validateEmailSubject(emailRequest);
        validateBodyAndTemplateConstraints(emailRequest);

        return evaluateViolations(context);
    }

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

    private void validateEmailSubject(EmailRequest emailRequest) {
        if(!isFieldValid(emailRequest.getSubject())) {
            this.addViolationMessage("The 'subject' field is required");
        }
    }

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

    public boolean isInvalidEmail(Collection<String> emailAddressCollection, boolean required) {
        if (!isFieldValid(emailAddressCollection)) {
            return required; // Valid if not required
        }
        return emailAddressCollection.stream().noneMatch(emailAddress -> EMAIL_PATTERN.matcher(emailAddress).matches());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isFieldValid(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    private boolean isFieldValid(Map<?, ?> collection) {
        return collection != null && !collection.isEmpty();
    }

    private boolean isFieldValid(String value) {
        return value != null && !value.isBlank();
    }

    private void addViolationMessage(String message) {
        this.violations.add(message);
    }

    @SuppressWarnings("SameParameterValue")
    private void addViolationMessage(String template, Object... args) {
        this.addViolationMessage(String.format(template, args));
    }

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
