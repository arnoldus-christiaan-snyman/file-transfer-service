package com.github.acs.file.email;

import com.github.acs.file.email.internal.EmailServiceBean;
import com.github.acs.file.email.internal.EmailProperties;
import com.github.acs.file.email.util.MimeMultipartUtils;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import com.github.acs.file.email.internal.EmailTemplateProcessor;
import org.springframework.boot.test.context.SpringBootTest;

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
class EmailServiceIntegrationTest {

    private static GreenMail smtpServer;

    @BeforeAll
    public static void setupSmtpServer() {
        ServerSetup serverSetup = new ServerSetup(25, "localhost", "smtp");
        smtpServer = new GreenMail(serverSetup);
        smtpServer.setUser("username", "password");
        smtpServer.start();
    }

    @AfterAll
    public static void stopSmtpServer() {
        smtpServer.stop();
    }

    @Autowired
    private EmailService emailService;

    @Test
    void testSendEmailWithBody() throws MessagingException, IOException {
        final var subject = "Integration Test Subject";
        final var messageBody = "This is a test email.";

        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject(subject)
                .body(messageBody)
                .build();

        this.emailService.sendEmail(emailRequest);

        assertNotNull(smtpServer.getReceivedMessages());
        assertTrue(smtpServer.getReceivedMessages().length > 0);
        var receivedMessage = smtpServer.getReceivedMessages()[0];
        MimeMultipart receivedMessageBody = (MimeMultipart) receivedMessage.getContent();
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

        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject(subject)
                .templateVariables(
                        Map.of("name", nameVariable, "message", messageVariable)
                )
                .build();

        this.emailService.sendEmail(emailRequest);

        assertNotNull(smtpServer.getReceivedMessages());
        assertTrue(smtpServer.getReceivedMessages().length > 0);
        var receivedMessage = smtpServer.getReceivedMessages()[0];
        MimeMultipart receivedMessageBody = (MimeMultipart) receivedMessage.getContent();
        var actualMessageText = MimeMultipartUtils.getTextFromMimeMultipart(receivedMessageBody);

        assertNotNull(receivedMessage);
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(subject, receivedMessage.getSubject());
        assertThat(actualMessageText).isEqualToIgnoringNewLines(expectedMessageText);

    }

    @Test
    void testSendEmailWithNoVariableTemplate() throws MessagingException, IOException {
        final var subject = "Integration Test Subject with Template";
        final var expectedMessageText = """
                <!DOCTYPE html>
                <html>
                    <head>
                        <title>Email Template without Variables</title>
                    </head>
                    <body>
                        <p>This is a test message without variables</p>
                    </body>
                </html>""";

        EmailRequest emailRequest = EmailRequest.builder()
                .to(Set.of("recipient@example.com"))
                .subject(subject)
                .build();

        this.emailService.sendEmail(emailRequest);

        assertNotNull(smtpServer.getReceivedMessages());
        assertTrue(smtpServer.getReceivedMessages().length > 0);
        var receivedMessage = smtpServer.getReceivedMessages()[0];
        MimeMultipart receivedMessageBody = (MimeMultipart) receivedMessage.getContent();
        var actualMessageText = MimeMultipartUtils.getTextFromMimeMultipart(receivedMessageBody);

        assertNotNull(receivedMessage);
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals(subject, receivedMessage.getSubject());
        assertThat(actualMessageText).isEqualToIgnoringNewLines(expectedMessageText);

    }

    private static String getTemplateMessageBodyText(String nameVariable, String messageVariable) {
        final var messageTemplate = """
                <!DOCTYPE html>
                <html>
                    <head>
                        <title>Email</title>
                    </head>
                    <body>
                        <p>Dear <span>%s</span>,</p>
                        <p>Thank you for your message: <span>%s</span>.</p>
                    </body>
                </html>""";
        return String.format(messageTemplate, nameVariable, messageVariable);
    }
}