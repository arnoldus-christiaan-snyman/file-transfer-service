package com.github.acs.file.email.internal;

import com.github.acs.file.email.EmailRequest;
import com.github.acs.file.email.EmailService;
import com.github.acs.file.email.EmailServiceException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class EmailServiceBean implements EmailService {

    private final EmailProperties emailProperties;
    private final JavaMailSender mailSender;
    private final EmailTemplateProcessor emailTemplateProcessor;

    @Override
    public void sendEmail(EmailRequest emailRequest) throws EmailServiceException {
        try {
            MimeMessage message = createMimeMessage(emailRequest);
            this.mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailServiceException("Error creating email message", e);
        } catch (MailSendException e) {
            throw new EmailServiceException("Error sending email", e);
        } catch (Exception e) {
            throw new EmailServiceException("Unexpected error occurred while sending email", e);
        }

    }

    String setEmailText(final EmailRequest emailRequest) {
        var request = EmailTemplateRequest.builder()
                .templateName("email-template")
                .templateVariables(emailRequest.getTemplateVariables())
                .body(emailRequest.getBody())
                .build();

        return this.emailTemplateProcessor.setEmailText(request);
    }

    MimeMessage createMimeMessage(@Valid final EmailRequest emailRequest) throws MessagingException {
        MimeMessage message = this.mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(this.emailProperties.getFromAddress());
        helper.setTo(emailRequest.getTo().toArray(new String[0]));
        helper.setSubject(emailRequest.getSubject());

        var body = setEmailText(emailRequest);
        helper.setText(body, true);

        return message;
    }
}
