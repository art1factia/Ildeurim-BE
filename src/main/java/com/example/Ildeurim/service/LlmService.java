package com.example.Ildeurim.service;

import com.example.Ildeurim.gpt.OpenAiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LlmService {
    private final OpenAIClient client;
    private final ObjectMapper om = new ObjectMapper();

    public LlmService(OpenAiClientFactory f) {
        this.client = f.client();
    }

    /** 시스템+유저 메시지를 한 번에 입력하는 헬퍼 */
    private static List<ResponseInputItem> buildMessages(String system, String user) {
        return List.of(
                ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .role(ResponseInputItem.Message.Role.SYSTEM)     // 문자열 X → enum O
                                .addInputTextContent(system)                     // addInputText → addInputTextContent
                                .build()
                ),
                ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .role(ResponseInputItem.Message.Role.USER)
                                .addInputTextContent(user)
                                .build()
                )
        );
    }

    public String complete(String system, String user, int maxTokens) {
        List<ResponseInputItem> messages = buildMessages(system, user);

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(ChatModel.GPT_4_1_MINI)                           // 모델
                .input(ResponseCreateParams.Input.ofResponse(messages))  // ← .input은 한 번만!
                .maxOutputTokens(maxTokens)
                .build();

        Response r = client.responses().create(params);
//        return r.outputText().orElse("");
        return r.output().toString();
    }

    public <T> T completeJson(String system, String user, Class<T> klass) {
        String raw = complete(system + "\n\n규칙: 반드시 유효한 JSON만. 코드블록 금지.", user, 1200);
        try {
            return om.readValue(raw, klass);
        } catch (Exception e) {
            // JSON 보정 1회
            String fixed = complete(
                    "You fix malformed JSON. Return ONLY valid minified JSON.",
                    raw, 800
            );
            try {
                return om.readValue(fixed, klass);
            } catch (Exception ex) {
                throw new RuntimeException("JSON parse failed", ex);
            }
        }
    }
}
