package com.github.acs.file.email.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.TemplateEngine;

@Component
@RequiredArgsConstructor
@Validated
public class EmailTemplateProcessor {

    private final TemplateEngine templateEngine;

    String setEmailText(@Valid final EmailTemplateRequest templateRequest) {
        String body = templateRequest.getBody();
        if (body == null || body.isEmpty()) {
            var context = new org.thymeleaf.context.Context();
            if (templateRequest.getTemplateVariables() != null && !templateRequest.getTemplateVariables().isEmpty()) {
                context.setVariables(templateRequest.getTemplateVariables());
            }

            body = this.templateEngine.process(templateRequest.getTemplateName().trim(), context);
        }
        return body;
    }

}
