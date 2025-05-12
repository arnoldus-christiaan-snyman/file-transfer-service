package com.github.acs.file.email;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendEmail(EmailRequest emailRequest) throws MessagingException;

}
