package com.example.Ildeurim.commons.converter;

import com.example.Ildeurim.domain.quickAnswer.QuestionItem;
import com.example.Ildeurim.domain.quickAnswer.QuestionList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter(autoApply = true)
public class QuestionListJsonConverter implements AttributeConverter<QuestionList, String> {

    // ★ 스프링이 셋업해줄 ObjectMapper (전역 설정 반영)
    private static ObjectMapper MAPPER;

    // ★ 애플리케이션 시작 시 스프링이 호출해 줄 세터
    public static void setObjectMapper(ObjectMapper om) {
        MAPPER = om;
    }

    // 안전한 접근자 (테스트/비상용 fallback 포함)
    private static ObjectMapper mapper() {
        if (MAPPER == null) {
            // Fallback: 스프링 컨텍스트 밖(순수 테스트 등)에서도 최소 동작 보장
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            om.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            om.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            om.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            MAPPER = om;
        }
        return MAPPER;
    }

    private static final TypeReference<List<QuestionItem>> LIST_TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(QuestionList attribute) {
        try {
            if (attribute == null || attribute.items() == null) {
                return "[]"; // 항상 JSON 배열로 저장
            }
            return mapper().writeValueAsString(attribute.items());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to write QuestionList as JSON array", e);
        }
    }

    @Override
    public QuestionList convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) {
                return QuestionList.empty();
            }
            var node = mapper().readTree(dbData);

            List<QuestionItem> items;
            if (node.isArray()) {
                items = mapper().convertValue(node, LIST_TYPE);
            } else if (node.isObject() && node.has("items") && node.get("items").isArray()) {
                // 구형 포맷: {"items":[...]}
                items = mapper().convertValue(node.get("items"), LIST_TYPE);
            } else {
                items = Collections.emptyList();
            }
            return new QuestionList(items);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read QuestionList JSON", e);
        }
    }
}
