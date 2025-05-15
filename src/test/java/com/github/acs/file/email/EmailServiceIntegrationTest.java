package com.github.acs.file.email;

import com.github.acs.file.email.internal.EmailServiceBean;
import com.github.acs.file.email.internal.EmailProperties;
import com.github.acs.file.email.util.MimeMultipartUtils;
import com.github.acs.file.email.util.TestEmailTemplate;
import com.github.acs.file.email.util.TestTemplateVariables;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import com.github.acs.file.email.internal.template.EmailTemplateProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        EmailServiceBean.class,
        EmailProperties.class,
        EmailTemplateProcessor.class,
        ThymeleafAutoConfiguration.class,
        MailSenderAutoConfiguration.class
})
@ActiveProfiles("test")
class EmailServiceIntegrationTest {

    private static GreenMail smtpServer;

    @BeforeAll
    static void setupSmtpServer() {
        var serverSetup = new ServerSetup(25, "localhost", "smtp");
        smtpServer = new GreenMail(serverSetup);
        smtpServer.setUser("username", "password");
        smtpServer.start();
    }

    @AfterAll
    static void stopSmtpServer() {
        smtpServer.stop();
    }

    @BeforeEach
    void setEmailService() throws FolderException {
        clearEmailBox();
    }

    private static void clearEmailBox() throws FolderException {
        smtpServer.purgeEmailFromAllMailboxes();
    }

    @Autowired
    private EmailService emailService;

    @Test
    void testSendEmailWithBody() throws MessagingException, IOException {
        final var subject = "Integration Test Subject with Template";
        final var messageBody = "This is a test email.";

        var emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject(subject)
                .body(messageBody)
                .build();

        this.emailService.sendEmail(emailRequest);

        assertNotNull(smtpServer.getReceivedMessages());
        assertTrue(smtpServer.getReceivedMessages().length > 0);
        var receivedMessage = smtpServer.getReceivedMessages()[0];
        var receivedMessageBody = (MimeMultipart) receivedMessage.getContent();
        var body = MimeMultipartUtils.getTextFromMimeMultipart(receivedMessageBody);

        assertNotNull(receivedMessage);
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(subject, receivedMessage.getSubject());
        assertEquals(messageBody, body);
    }

    @Test
    void testSendEmailWithTemplate() throws MessagingException, IOException {
        final var subject = "Integration Test Subject with Template";
        final var nameVariable = "John Snow";
        final var messageVariable = "You know nothing";
        final var expectedMessageText = getTemplateMessageBodyText(nameVariable, messageVariable);

        var emailTemplateVariables = TestTemplateVariables.builder()
                .variables(
                        Map.of("name", nameVariable, "message", messageVariable)
                )
                .build();

        var emailTemplate = TestEmailTemplate.builder()
                .templateName("email-template")
                .templateVariables(emailTemplateVariables)
                .build();

        var emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject(subject)
                .template(emailTemplate)
                .build();

        this.emailService.sendEmail(emailRequest);

        assertNotNull(smtpServer.getReceivedMessages());
        assertTrue(smtpServer.getReceivedMessages().length > 0);
        var receivedMessage = smtpServer.getReceivedMessages()[0];
        var receivedMessageBody = (MimeMultipart) receivedMessage.getContent();
        var actualMessageText = MimeMultipartUtils.getTextFromMimeMultipart(receivedMessageBody);

        assertNotNull(receivedMessage);
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(subject, receivedMessage.getSubject());
        assertThat(actualMessageText).isEqualToIgnoringNewLines(expectedMessageText);

    }

    @SuppressWarnings("SameParameterValue")
    private static String getTemplateMessageBodyText(String nameVariable, String messageVariable) {
        final var messageTemplate = """
                <!DOCTYPE html>
                <html lang="en">
                    <head>
                        <title>Email</title>
                        <meta content="text/html; charset=utf-8" />
                    </head>
                    <body>
                        <p>Dear <span>%s</span>,</p>
                        <p>Thank you for your message: <span>%s</span>.</p>
                    </body>
                </html>""";
        return String.format(messageTemplate, nameVariable, messageVariable);
    }
}