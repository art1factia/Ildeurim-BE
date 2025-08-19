package com.example.Ildeurim.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;
    @Value("${twilio.auth-token}")
    private String authToken;
    @Value("${twilio.phone-number}")
    private String fromPhone;

    private final Map<String, String> verificationStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendVerificationCode(String phoneNumber) {
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        verificationStore.put(phoneNumber, code);

        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(fromPhone),
                "Your verification code is: " + code
        ).create();
    }

    public boolean verifyCode(String phoneNumber, String code) {
        return code.equals(verificationStore.get(phoneNumber));
    }
}
