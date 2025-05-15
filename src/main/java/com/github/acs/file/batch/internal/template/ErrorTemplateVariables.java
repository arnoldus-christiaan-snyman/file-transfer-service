package com.github.acs.file.batch.internal.template;

import com.github.acs.file.email.TemplateVariables;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorTemplateVariables(
        LocalDateTime timeOfDay,
        String jobName,
        String errorMessage,
        LocalDateTime failureTime
) implements TemplateVariables {
}
