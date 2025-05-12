package com.github.acs.file.email;

public interface EmailService {

    void sendEmail(EmailRequest emailRequest) throws EmailServiceException;

}
