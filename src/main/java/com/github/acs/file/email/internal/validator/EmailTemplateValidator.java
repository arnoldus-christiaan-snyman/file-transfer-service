package com.github.acs.file.email.internal.validator;

import com.github.acs.file.email.EmailTemplate;

public final class EmailTemplateValidator extends BaseValidator<ValidEmailTemplate, EmailTemplate> {
    @Override
    protected void validate(EmailTemplate validationEntity) {
        if(!isFieldValid(validationEntity.templateName())) {
            super.addViolationMessage("The 'template name' field is required");
        }

        if(validationEntity.templateVariables() == null) {
            super.addViolationMessage("The 'template variable' field is required");
        }
    }
}
