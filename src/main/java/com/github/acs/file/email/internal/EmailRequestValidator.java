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

    @Override
    public boolean isValid(EmailRequest emailRequest, ConstraintValidatorContext context) {
        boolean hasInvalidRecipientAddress = isInvalidEmail(emailRequest.getTo(), true);
        boolean hasInvalidCCAddress = isInvalidEmail(emailRequest.getCc(), false);
        boolean hasInvalidBCCAddress = isInvalidEmail(emailRequest.getBcc(), false);

        boolean hasBody = emailRequest.getBody() != null && !emailRequest.getBody().isEmpty();
        boolean isMissingSubject = emailRequest.getSubject() == null || emailRequest.getSubject().isEmpty();
        boolean hasTemplateName = emailRequest.getTemplateName() != null && !emailRequest.getTemplateName().isEmpty();
        boolean hasTemplateVariables = emailRequest.getTemplateVariables() != null && !emailRequest.getTemplateVariables().isEmpty();

        SequencedSet<String> violations = new LinkedHashSet<>();
        if(hasInvalidRecipientAddress) {
            violations.add("The 'to' recipient field is missing or have one or more invalid email(s)");
        }

        if(hasInvalidCCAddress) {
            violations.add("The 'cc' recipient field has one or more invalid email(s)");
        }

        if(hasInvalidBCCAddress) {
            violations.add("The 'bcc' recipient field has one or more invalid email(s)");
        }

        if(isMissingSubject) {
            violations.add("The 'subject' field is required.");
        }

        if (hasBody && hasTemplateName) {
            violations.add("Only the 'body' or 'templateName' can be set, not both");
        }

        if (hasBody && hasTemplateVariables) {
            violations.add("If 'body' is set, 'templateVariables' must be empty");
        }

        if (!(hasBody || (hasTemplateName || hasTemplateVariables))) {
            violations.add("At least a 'body' or 'templateName' or 'templateVariables' must be set");
        }

        if(!violations.isEmpty()) {
            setViolationMessage(context, violations);
            return false;
        }
        return true;
    }

    public boolean isInvalidEmail(Collection<String> emailAddressCollection, boolean required) {
        if (emailAddressCollection == null || emailAddressCollection.isEmpty()) {
            return required; // Valid if not required
        }
        return emailAddressCollection.stream().noneMatch(emailAddress -> EMAIL_PATTERN.matcher(emailAddress).matches());
    }

    private void setViolationMessage(ConstraintValidatorContext context, SequencedSet<String> violations) {
        context.disableDefaultConstraintViolation();
        violations.reversed();
        for(String message : violations) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
    }
}
