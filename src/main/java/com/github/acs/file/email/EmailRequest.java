package com.github.acs.file.email;

import com.github.acs.file.email.internal.validator.ValidEmailRequest;
import jakarta.validation.constraints.Email;
import lombok.Builder;

import java.util.Set;

@Builder
@ValidEmailRequest
public record EmailRequest (
        Set<String> to,
        Set<String> cc,
        Set<String> bcc,
        String subject,
        String body,
        EmailTemplate template
)
{ }
