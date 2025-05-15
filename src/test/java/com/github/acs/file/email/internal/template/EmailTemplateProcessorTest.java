package com.github.acs.file.email.internal.template;

import com.github.acs.file.email.util.TestEmailTemplate;
import com.github.acs.file.email.util.TestTemplateVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EmailTemplateProcessor.class)
@ImportAutoConfiguration(ThymeleafAutoConfiguration.class)
@ActiveProfiles("test")
class EmailTemplateProcessorTest {

    @Autowired
    private EmailTemplateProcessor emailTemplateProcessor;

    @Test
    void testSendEmailWithThymeleafTemplateWithoutVariables() {
        var emailTemplate = TestEmailTemplate.builder()
                .templateName("email-template-without-variables")
                .build();

        var request = EmailTemplateRequest.builder()
                .template(emailTemplate)
                .build();

        String expectedResult = """
                <!DOCTYPE html>
                <html lang="en">
                    <head>
                        <title>Email Template without Variables</title>
                        <meta content="text/html; charset=utf-8" />
                    </head>
                    <body>
                        <p>This is a test message without variables</p>
                    </body>
                </html>""";

        var actualResult = this.emailTemplateProcessor.setEmailText(request);

        assertNotNull(actualResult);
        assertThat(actualResult).isEqualToIgnoringNewLines(expectedResult);
    }

    @Test
    void testSendEmailWithThymeleafTemplateWithVariables() {
        var emailTemplateVariables = TestTemplateVariables.builder()
                .variables(
                        Map.of("name", "John Snow", "message", "You know nothing")
                )
                .build();

        var emailTemplate = TestEmailTemplate.builder()
                .templateName("email-template")
                .templateVariables(emailTemplateVariables)
                .build();

        var request = EmailTemplateRequest.builder()
                .template(emailTemplate)
                .build();

        String expectedResult = """
                <!DOCTYPE html>
                <html lang="en">
                    <head>
                        <title>Email</title>
                        <meta content="text/html; charset=utf-8" />
                    </head>
                    <body>
                        <p>Dear <span>John Snow</span>,</p>
                        <p>Thank you for your message: <span>You know nothing</span>.</p>
                    </body>
                </html>""";

        var actualResult = this.emailTemplateProcessor.setEmailText(request);

        assertNotNull(actualResult);
        assertThat(actualResult).isEqualToIgnoringNewLines(expectedResult);
    }

}