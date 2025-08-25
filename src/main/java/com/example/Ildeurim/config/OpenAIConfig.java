package com.example.Ildeurim.config;

import com.example.Ildeurim.gpt.OpenAiClientFactory;
import com.openai.client.OpenAIClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// OpenAIConfig.java (선택)
@Configuration
public class OpenAIConfig {
    private final OpenAiClientFactory factory;
    public OpenAIConfig(OpenAiClientFactory factory) { this.factory = factory; }

    @Bean
    public OpenAIClient openAIClient() {
        return factory.client();
    }
}
