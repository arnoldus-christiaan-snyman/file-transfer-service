package com.github.acs.file.email.util;

import com.github.acs.file.email.TemplateVariables;
import lombok.Builder;

import java.util.Map;

@Builder
public record TestTemplateVariables(
        Map<String, Object> variables
) implements TemplateVariables {

    @Override
    public Map<String, Object> getVariables() {
        return variables;
    }
}
