package com.github.acs.file.batch.internal.template;

import com.github.acs.file.email.EmailTemplate;
import lombok.Builder;

@Builder
public record CompletedEmailTemplate(CompletedTemplateVariables completedTemplateVariables) implements EmailTemplate {

    public static final String COMPLETED_EMAIL_TEMPLATE = "batch-complete-email";

    @Override
    public String templateName() {
        return COMPLETED_EMAIL_TEMPLATE;
    }

    @Override
    public CompletedTemplateVariables templateVariables() {
        return completedTemplateVariables;
    }
}
