package com.github.acs.file.email.internal.validator;

import com.github.acs.file.email.internal.template.EmailTemplateRequest;
import org.springframework.validation.annotation.Validated;

@Validated
public final class EmailTemplateRequestValidator extends BaseValidator<ValidEmailTemplateRequest, EmailTemplateRequest> {

    @Override
    protected void validate(EmailTemplateRequest templateRequest) {
        boolean hasBody = isFieldValid(templateRequest.body());
        boolean hasEmailTemplate = templateRequest.template() != null;

        if (!(hasBody || hasEmailTemplate)) {
            super.addViolationMessage("At least a the 'body' or 'template' field must be set");
        } else if (hasBody && hasEmailTemplate) {
            super.addViolationMessage("Only the 'body' or 'template' can be set, not both");
        }
    }

}
