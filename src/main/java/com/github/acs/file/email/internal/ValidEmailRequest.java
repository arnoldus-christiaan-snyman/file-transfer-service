package com.github.acs.file.email.internal;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailRequestValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmailRequest {

    String message() default "Invalid EmailRequest: Either 'body' or 'templateName' must be set, but not both. If 'body' is set, 'templateVariables' must be empty.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}