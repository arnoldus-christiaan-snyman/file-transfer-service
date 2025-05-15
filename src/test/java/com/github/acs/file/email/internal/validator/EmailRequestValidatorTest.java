package com.github.acs.file.email.internal.validator;

import com.github.acs.file.email.EmailRequest;
import com.github.acs.file.email.util.TestEmailTemplate;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class EmailRequestValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmailWithMissingToRecipientEmailAddress() {
        var emailRequest = EmailRequest.builder()
                .to(Set.of())
                .subject("Test Subject")
                .body("Test Body")
                .build();

        var violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("At least one valid 'to' recipient is required", violations.iterator().next().getMessage());
    }

    @Test
    void testSendEmailWithInvalidToRecipientEmailAddresses() {
        var emailRequest = EmailRequest.builder()
                .to(Set.of("invalid-email"))
                .subject("Test Subject")
                .body("Test Body")
                .build();

        var violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'to' recipient field has one or more invalid email(s)", violations.iterator().next().getMessage());
    }

    @Test
    void testSendEmailWithInvalidCcRecipientEmailAddresses() {
        var emailRequest = EmailRequest.builder()
                .to(Set.of("test@acs.com"))
                .cc(Set.of("invalid-address"))
                .subject("Test Subject")
                .body("Test Body")
                .build();

        var violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'cc' recipient field has one or more invalid email(s)", violations.iterator().next().getMessage());

    }

    @Test
    void testSendEmailWithMissingSubjectField() {
        var emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("")
                .body("Test Body")
                .build();

        var violations = this.validator.validate(emailRequest);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("The 'subject' field is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidationWithoutMultipleErrors() {
        var emailTemplate = TestEmailTemplate.builder()
                .build();

        var emailRequest = EmailRequest.builder()
                .to(Set.of())
                .cc(Set.of("invalid-email"))
                .subject("")
                .body("Test Body")
                .template(emailTemplate)
                .build();

        var violations = this.validator.validate(emailRequest);

        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());
    }

}