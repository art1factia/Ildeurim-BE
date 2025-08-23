package com.example.Ildeurim.twilio;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@Getter @Setter
public class TwilioProperties {
    private String accountSid;
    private String authToken;
    private String verifyServiceSid;
}
