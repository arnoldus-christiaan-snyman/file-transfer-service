package com.github.acs.file.email;

import com.github.acs.file.email.util.TestEmailTemplate;
import com.github.acs.file.email.util.TestTemplateVariables;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmailTemplateTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testEmailTemplateWithoutNameAndVariables() {
        var emailTemplate = TestEmailTemplate.builder().build();

        var violations = this.validator.validate(emailTemplate);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    @Test
    void testEmailTemplateWithNameIsNull() {
        var emailTemplateVariables = TestTemplateVariables.builder()
                .variables(
                        Map.of("name", "John Snow", "message", "You know nothing")
                )
                .build();

        var emailTemplate = TestEmailTemplate.builder()
                .templateVariables(emailTemplateVariables)
                .build();

        var violations = this.validator.validate(emailTemplate);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'template name' field is required", violations.iterator().next().getMessage());
    }

    @Test
    void testEmailTemplateWithVariablesIsNull() {
        var emailTemplate = TestEmailTemplate.builder()
                .templateName("email-template-without-variables")
                .build();

        var violations = this.validator.validate(emailTemplate);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'template variable' field is required", violations.iterator().next().getMessage());
    }

    @Test
    void testEmailTemplateWithEmptyVariables() {
        var emailTemplateVariables = TestTemplateVariables.builder()
                .variables(
                        Map.of()
                )
                .build();

        var emailTemplate = TestEmailTemplate.builder()
                .templateName("email-template-without-variables")
                .templateVariables(emailTemplateVariables)
                .build();

        var violations = this.validator.validate(emailTemplate);
        assertTrue(violations.isEmpty());
    }
}