package com.github.acs.file.email.internal.validator;

import com.github.acs.file.email.EmailRequest;

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
public final class EmailRequestValidator extends BaseValidator<ValidEmailRequest, EmailRequest> {

    /**
     * Regular expression pattern for validating email addresses.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    /**
     * Validates the given {@link EmailRequest} object against the defined constraints.
     *
     * @param emailRequest the email request to validate
     */
    @Override
    protected void validate(EmailRequest emailRequest) {
        validateEmailRecipients(emailRequest);
        validateEmailSubject(emailRequest);
    }

    /**
     * Validates the email recipient fields ('to', 'cc', 'bcc') in the email request.
     *
     * @param emailRequest the email request to validate
     */
    private void validateEmailRecipients(EmailRequest emailRequest) {
        final String invalidEmailAddressMessage = "The '%s' recipient field has one or more invalid email(s)";
        if(emailRequest.to() == null) {
            super.addViolationMessage(DEFAULT_REQUIRED_FIELD_MESSAGE, "to");
        }else if(emailRequest.to().isEmpty()) {
            super.addViolationMessage("At least one valid 'to' recipient is required");
        } else if(isInvalidEmail(emailRequest.to(), true)) {
            super.addViolationMessage(invalidEmailAddressMessage, "to");
        }

        if(isInvalidEmail(emailRequest.cc(), false)) {
            super.addViolationMessage(invalidEmailAddressMessage, "cc");
        }

        if(isInvalidEmail(emailRequest.bcc(), false)) {
            super.addViolationMessage(invalidEmailAddressMessage, "bcc");
        }
    }

    /**
     * Validates the 'subject' field in the email request.
     *
     * @param emailRequest the email request to validate
     */
    private void validateEmailSubject(EmailRequest emailRequest) {
        if(!isFieldValid(emailRequest.subject())) {
            super.addViolationMessage(DEFAULT_REQUIRED_FIELD_MESSAGE, "subject");
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

}
