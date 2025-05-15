package com.github.acs.file.batch.internal.template;

import com.github.acs.file.email.EmailTemplate;
import lombok.Builder;

@Builder
public record ErrorEmailTemplate(ErrorTemplateVariables errorTemplateVariables) implements EmailTemplate {

    public static final  String FAILED_EMAIL_TEMPLATE = "batch-error-email";

    @Override
    public String templateName() {
        return FAILED_EMAIL_TEMPLATE;
    }

    @Override
    public ErrorTemplateVariables templateVariables() {
        return errorTemplateVariables;
    }
}
