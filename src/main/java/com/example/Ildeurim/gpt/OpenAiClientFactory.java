package com.example.Ildeurim.gpt;
// OpenAiClientFactory.java
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.stereotype.Component;

@Component
public class OpenAiClientFactory {
    public OpenAIClient client() {
        // OPENAI_API_KEY 환경변수 사용
        return OpenAIOkHttpClient.fromEnv();
    }
}
