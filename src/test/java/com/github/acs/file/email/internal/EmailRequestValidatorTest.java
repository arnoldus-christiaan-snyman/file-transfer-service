package com.github.acs.file.email.internal;

import com.github.acs.file.email.EmailRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmailRequestValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testSendEmailWithMissingToRecipientEmailAddress() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of())
                .subject("Test Subject")
                .body("Test Body")
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'to' recipient field is missing or have one or more invalid email(s)", violations.iterator().next().getMessage());
    }

    @Test
    void testSendEmailWithInvalidToRecipientEmailAddresses() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("invalid-email"))
                .subject("Test Subject")
                .body("Test Body")
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'to' recipient field is missing or have one or more invalid email(s)", violations.iterator().next().getMessage());
    }

    @Test
    void testSendEmailWithInvalidCcRecipientEmailAddresses() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("test@acs.com"))
                .cc(Set.of("invalid-address"))
                .subject("Test Subject")
                .body("Test Body")
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'cc' recipient field has one or more invalid email(s)", violations.iterator().next().getMessage());

    }

    @Test
    void testSendEmailWithMissingSubjectField() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("")
                .body("Test Body")
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'subject' field is required.", violations.iterator().next().getMessage());
    }


    @Test
    void testValidationWithBodyAndTemplateVariables() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("test@acs.nl"))
                .subject("Test Subject")
                .body("Test Body")
                .templateVariables(Map.of("invalid-variable", "value"))
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("If 'body' is set, 'templateVariables' must be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testValidationWithBodyAndTemplateNamer() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("test@acs.nl"))
                .subject("Test Subject")
                .body("Test Body")
                .templateName("test-template")
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Only the 'body' or 'templateName' can be set, not both", violations.iterator().next().getMessage());
    }

    @Test
    void testValidationWithoutBodyOrTemplateName() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("test@acs.nl"))
                .subject("Test Subject")
                .body("Test Body")
                .templateName("test-template")
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Only the 'body' or 'templateName' can be set, not both", violations.iterator().next().getMessage());
    }

    @Test
    void testValidationWithoutBodyOrTemplateNameOrTemplateVariables() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("test@acs.nl"))
                .subject("Test Subject")
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("At least a 'body' or 'templateName' or 'templateVariables' must be set", violations.iterator().next().getMessage());
    }

    @Test
    void testValidationWithoutMultipleErrors() {
        String expectedResults = """
                The 'to' recipient field is missing or have one or more invalid email(s)
                The 'cc' recipient field has one or more invalid email(s)
                The 'subject' field is required.
                If 'body' is set, 'templateVariables' must be empty""";

        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of())
                .cc(Set.of("invalid-email"))
                .subject("")
                .body("Test Body")
                .templateVariables(Map.of("invalid-key", "invalid-value"))
                .build();

        Set<ConstraintViolation<EmailRequest>> violations = this.validator.validate(emailRequest);
        StringBuilder stringBuilder = new StringBuilder();
        for(ConstraintViolation<EmailRequest> violation : violations) {
            stringBuilder.append(violation.getMessage()).append("\n");
        }

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() > 1);
        assertEquals(expectedResults, stringBuilder.toString().trim());
    }

}