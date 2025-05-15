package com.github.acs.file.email.util;

import com.github.acs.file.email.EmailTemplate;
import lombok.Builder;

@Builder
public record TestEmailTemplate(
        String templateName,
        TestTemplateVariables templateVariables
) implements EmailTemplate {
    @Override
    public String templateName() {
        return templateName;
    }

    @Override
    public TestTemplateVariables templateVariables() {
        return templateVariables;
    }
}
