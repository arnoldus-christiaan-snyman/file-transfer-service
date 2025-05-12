package com.github.acs.file.email.internal;

import com.github.acs.file.email.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class EmailServiceBeanTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailTemplateProcessor emailTemplateProcessor;

    @Mock
    private EmailProperties emailProperties;

    @InjectMocks
    private EmailServiceBean emailService;

    private MimeMessage mimeMessage;

    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.mimeMessage = mock(MimeMessage.class);

        when(emailTemplateProcessor.setEmailText(any(EmailTemplateRequest.class))).thenReturn("This is a test message");
        when(emailProperties.getFromAddress()).thenReturn("test@acs.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendEmailWithValidBodyInputs() throws MessagingException {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("Test Subject")
                .body("Test Body")
                .build();

        emailService.sendEmail(emailRequest);
        verify(mailSender, times(1)).send(this.mimeMessage);
    }

    @Test
    void testSendEmailWithValidTemplateWithoutVariables() throws MessagingException {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("Test Subject")
                .templateName("email-template-without-variables")
                .build();

        emailService.sendEmail(emailRequest);
        verify(mailSender, times(1)).send(this.mimeMessage);
    }

    @Test
    void testSendEmailWithValidTemplateVariables() throws MessagingException {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("Test Subject")
                .templateVariables(Map.of("name", "John Snow", "message", "You know nothing"))
                .build();

        emailService.sendEmail(emailRequest);
        verify(mailSender, times(1)).send(this.mimeMessage);
    }

}