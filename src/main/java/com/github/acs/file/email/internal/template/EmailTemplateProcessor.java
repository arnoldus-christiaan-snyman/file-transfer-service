package com.github.acs.file.email.internal.template;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
@Validated
public class EmailTemplateProcessor {

    private final TemplateEngine templateEngine;

    public String setEmailText(@Valid final EmailTemplateRequest templateRequest) {
        String body = templateRequest.body();
        if (body == null || body.isEmpty()) {
            try {
                var context = new Context();
                if (templateRequest.template().templateVariables() != null && !templateRequest.template().templateVariables().getVariables().isEmpty()) {
                    context.setVariables(templateRequest.template().templateVariables().getVariables());
                }
                body = this.templateEngine.process(templateRequest.template().templateName().trim(), context);
            }catch (Exception e) {
                throw new EmailTemplateException("Error processing email template", e);
            }
        }
        return body;
    }

}
