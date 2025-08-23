package com.example.Ildeurim.twilio;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TwilioConfig {

    private final TwilioProperties props;

    @PostConstruct
    public void init() {
        com.twilio.Twilio.init(props.getAccountSid(), props.getAuthToken());
    }

    @Bean
    public String twilioVerifyServiceSid(TwilioProperties p) { // 그냥 문자열 빈으로 노출
        return p.getVerifyServiceSid();
    }
}