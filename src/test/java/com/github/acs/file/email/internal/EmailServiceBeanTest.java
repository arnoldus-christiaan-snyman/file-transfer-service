package com.github.acs.file.email.internal;

import com.github.acs.file.email.EmailRequest;
import com.github.acs.file.email.EmailServiceException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mimeMessage = mock(MimeMessage.class);

        when(emailTemplateProcessor.setEmailText(any(EmailTemplateRequest.class))).thenReturn("This is a test message");
        when(emailProperties.getFromAddress()).thenReturn("test@acs.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendEmailWithValidBodyInputs() throws EmailServiceException {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("Test Subject")
                .body("Test Body")
                .build();

        emailService.sendEmail(emailRequest);
        verify(mailSender, times(1)).send(this.mimeMessage);
    }

    @Test
    void testSendEmailWithValidTemplateWithoutVariables() throws EmailServiceException {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("Test Subject")
                .templateName("email-template-without-variables")
                .build();

        emailService.sendEmail(emailRequest);
        verify(mailSender, times(1)).send(this.mimeMessage);
    }

    @Test
    void testSendEmailWithValidTemplateVariables() throws EmailServiceException {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("Test Subject")
                .templateVariables(Map.of("name", "John Snow", "message", "You know nothing"))
                .build();

        emailService.sendEmail(emailRequest);
        verify(mailSender, times(1)).send(this.mimeMessage);
    }

    @Test
    void testSendingEmailException() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("Test Subject")
                .body("Test Body")
                .build();

        doThrow(new MailSendException("Unable to send email")).when(this.mailSender).send(any(MimeMessage.class));

        EmailServiceException exception = assertThrows(EmailServiceException.class, () ->
            this.emailService.sendEmail(emailRequest)
        );
        assertEquals("Error sending email", exception.getMessage());
    }

    @Test
    void testCreateMessageException() {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject("Test Subject")
                .body("Test Body")
                .build();

        doThrow(new RuntimeException("Unable to send email")).when(this.mailSender).send(any(MimeMessage.class));

        EmailServiceException exception = assertThrows(EmailServiceException.class, () ->
            this.emailService.sendEmail(emailRequest));

        assertEquals("Unexpected error occurred while sending email", exception.getMessage());
    }

}