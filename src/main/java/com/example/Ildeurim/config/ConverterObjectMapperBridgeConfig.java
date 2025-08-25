package com.example.Ildeurim.config;

import com.example.Ildeurim.commons.converter.AnswerListJsonConverter;
import com.example.Ildeurim.commons.converter.QuestionListJsonConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterObjectMapperBridgeConfig {

    private final ObjectMapper objectMapper;

    public ConverterObjectMapperBridgeConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void hookUp() {
        // ★ 전역 ObjectMapper ↔ JPA 컨버터 연결 (앱 시작 시 1회)
        QuestionListJsonConverter.setObjectMapper(objectMapper);
        AnswerListJsonConverter.setObjectMapper(objectMapper);
    }
}
