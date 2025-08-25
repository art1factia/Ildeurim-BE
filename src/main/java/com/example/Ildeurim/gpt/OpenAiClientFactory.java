package com.example.Ildeurim.gpt;
// OpenAiClientFactory.java

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAiClientFactory {
    @Value("${openai.api-key:}")
    private String apiKey;

    // 필요시 org/project도 yml에서 읽어 같은 방식으로 처리
    // @Value("${openai.org-id:}") private String orgId;
    // @Value("${openai.project-id:}") private String projectId;

    public OpenAIClient client() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BeanCreationException("openAIClient",
                    "Missing OpenAI API key. Set 'OPENAI_API_KEY' env or 'openai.api-key' in application.yml");
        }

        // (A) builder로 직접 주입: fromEnv에 의존하지 않음
        return OpenAIOkHttpClient
                .builder()
                .apiKey(apiKey)
                .build();
    }

}
