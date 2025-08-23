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

//    @Value("${twilio.account-sid}")
//    private String accountSid;
//    @Value("${twilio.auth-token}")
//    private String authToken;
//    @Value("${twilio.verify-service-sid}")
//    String messagingServiceSid;


    private final Map<String, String> verificationStore = new ConcurrentHashMap<>();

//    @PostConstruct
//    public void init() {
//        Twilio.init(accountSid, authToken);
//    }

    public void sendVerificationCode(String phoneNumber) {
        if (phoneNumber.equals("+821012345678")) {
            verificationStore.put(phoneNumber, "111111");
        } else {
//            String code = String.valueOf(new Random().nextInt(899999) + 100000);
//            verificationStore.put(phoneNumber, code);
//            Message.creator(new PhoneNumber(phoneNumber), (PhoneNumber) null, "Your verification code is: " + code)
//                    .setMessagingServiceSid(messagingServiceSid)
//                    .create();
            verificationStore.put(phoneNumber, "222222");
        }
    }

    public boolean verifyCode(String phoneNumber, String code) {
        return code.equals(verificationStore.get(phoneNumber));
    }
}
