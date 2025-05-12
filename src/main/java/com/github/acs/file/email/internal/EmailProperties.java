package com.github.acs.file.email.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "module.email")
@NoArgsConstructor
@Data
public class EmailProperties {

    @NotBlank @Email
    private String fromAddress;

}
