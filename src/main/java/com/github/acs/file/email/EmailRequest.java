package com.github.acs.file.email;

import com.github.acs.file.email.internal.ValidEmailRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
@ValidEmailRequest
public class EmailRequest {

    private Set<String> to;

    private Set<String> cc;

    private Set<String> bcc;

    private String subject;

    private String body;

    private String templateName;

    private Map<String, Object> templateVariables;
}
