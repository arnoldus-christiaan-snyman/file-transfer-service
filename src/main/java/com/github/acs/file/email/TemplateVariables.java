package com.github.acs.file.email;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface TemplateVariables {

    default Map<String, Object> getVariables() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();
        if(fields.length < 1) {
            return Map.of();
        }
        Map<String, Object> variables = new HashMap<>();
        for(Field field : fields) {
            variables.put(field.getName(), field.get(this));
        }
        return variables;
    }

}
