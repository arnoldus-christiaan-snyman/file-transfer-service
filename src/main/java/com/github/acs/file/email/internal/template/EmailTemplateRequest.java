package com.github.acs.file.email.internal.template;

import com.github.acs.file.email.EmailTemplate;
import com.github.acs.file.email.internal.validator.ValidEmailTemplateRequest;
import lombok.Builder;
import lombok.Getter;

@Builder
@ValidEmailTemplateRequest
public record EmailTemplateRequest(
        String body,
        EmailTemplate template
)
{ }