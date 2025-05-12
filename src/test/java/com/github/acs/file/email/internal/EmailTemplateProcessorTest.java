package com.github.acs.file.email.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Map;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EmailTemplateProcessor.class)
@ImportAutoConfiguration(ThymeleafAutoConfiguration.class)
class EmailTemplateProcessorTest {

    @Autowired
    private EmailTemplateProcessor emailTemplateProcessor;

    @Test
    void testSendEmailWithThymeleafTemplateWithoutVariables() {
        var request = EmailTemplateRequest.builder()
                .templateName("email-template-without-variables")
                .build();

        String expectedResult = """
                <!DOCTYPE html>
                <html>
                    <head>
                        <title>Email Template without Variables</title>
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
        var request = EmailTemplateRequest.builder()
                .templateName("email-template")
                .templateVariables(Map.of("name", "John Snow", "message", "You know nothing"))
                .build();

        String expectedResult = """
                <!DOCTYPE html>
                <html>
                    <head>
                        <title>Email</title>
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