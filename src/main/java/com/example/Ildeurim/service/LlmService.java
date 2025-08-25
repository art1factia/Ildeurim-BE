package com.example.Ildeurim.service;

import com.example.Ildeurim.gpt.OpenAiClientFactory;
import com.example.Ildeurim.config.JacksonConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.*;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LlmService {
    private final OpenAIClient client;
    private final ObjectMapper om;

    public LlmService(OpenAIClient client, ObjectMapper om) {   // <- 팩토리 대신 클라이언트 자체를 주입
        this.client = client;
        this.om = om;
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
//        System.out.println(firstText(r).get());
//        return r.outputText().orElse("");
        return firstText(r).get();
    }

    public <T> T completeJson(String system, String user, Class<T> klass) {
        String raw = complete(system + "\n\n규칙: 반드시 유효한 JSON만. 코드블록 금지.", user, 1200);
        System.out.println("raw: " + raw);
        try {
            return om.readValue(raw, klass);
        } catch (Exception e) {
            // JSON 보정 1회
            String fixed = complete(
                    "You fix malformed JSON. Return ONLY valid minified JSON.",
                    raw, 800
            );
            System.out.println("fixed: " + fixed);
            try {
                return om.readValue(fixed, klass);
            } catch (Exception ex) {
                throw new RuntimeException("JSON parse failed", ex);
            }
        }
    }
    // 또는 첫 번째 텍스트만
    private static Optional<String> firstText(Response r) {
        return r.output().stream()
                .map(ResponseOutputItem::message)
                .flatMap(Optional::stream)
                .flatMap(msg -> msg.content().stream())
                .map(ResponseOutputMessage.Content::outputText)
                .flatMap(Optional::stream)
                .map(ResponseOutputText::text)
                .findFirst();
    }
}
