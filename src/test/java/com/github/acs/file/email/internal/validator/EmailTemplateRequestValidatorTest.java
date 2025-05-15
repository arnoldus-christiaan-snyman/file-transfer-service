package com.github.acs.file.email.internal.validator;

import com.github.acs.file.email.internal.template.EmailTemplateRequest;
import com.github.acs.file.email.util.TestEmailTemplate;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class EmailTemplateRequestValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidationWithoutBodyAndTemplate() {
        var templateRequest = EmailTemplateRequest.builder().build();

        var violations = this.validator.validate(templateRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("At least a the 'body' or 'template' field must be set", violations.iterator().next().getMessage());
    }

    @Test
    void testValidationWithBodyAndTemplate() {
        var emailTemplate = TestEmailTemplate.builder()
                .build();

        var templateRequest = EmailTemplateRequest.builder()
                .body("This is a test body")
                .template(emailTemplate)
                .build();

        var violations = this.validator.validate(templateRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Only the 'body' or 'template' can be set, not both", violations.iterator().next().getMessage());
    }

}