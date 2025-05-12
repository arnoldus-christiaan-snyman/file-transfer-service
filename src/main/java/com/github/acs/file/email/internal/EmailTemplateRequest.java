package com.github.acs.file.email.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
class EmailTemplateRequest {

    @NotBlank
    private String templateName;

    private String body;

    private Map<String, Object> templateVariables;

}
