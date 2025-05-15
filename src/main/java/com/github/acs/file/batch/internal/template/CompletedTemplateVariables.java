package com.github.acs.file.batch.internal.template;

import com.github.acs.file.email.TemplateVariables;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CompletedTemplateVariables(
        String timeOfDay,
        String jobName,
        LocalDateTime completedTime,
        int passedCount
) implements TemplateVariables {
}
