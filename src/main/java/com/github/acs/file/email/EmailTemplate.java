package com.github.acs.file.email;

import com.github.acs.file.email.internal.validator.ValidEmailTemplate;

@ValidEmailTemplate
public interface EmailTemplate {

    String templateName();

    TemplateVariables templateVariables();

}
