package com.example.Ildeurim.commons.converter;

import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter(autoApply = true)
public class AnswerListJsonConverter implements AttributeConverter<AnswerList, String> {

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

    private static final TypeReference<List<AnswerItem>> LIST_TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(AnswerList attribute) {
        try {
            // 항상 배열(JSON array)로 저장
            if (attribute == null || attribute.items() == null) {
                return "[]";
            }
            return MAPPER.writeValueAsString(attribute.items());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to write AnswerList as JSON array", e);
        }
    }

    @Override
    public AnswerList convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) {
                return AnswerList.empty();
            }
            JsonNode node = MAPPER.readTree(dbData);

            List<AnswerItem> items;
            if (node.isArray()) {
                items = MAPPER.convertValue(node, LIST_TYPE);
            } else if (node.isObject() && node.has("items") && node.get("items").isArray()) {
                // 구형 포맷: {"items":[...]}
                items = MAPPER.convertValue(node.get("items"), LIST_TYPE);
            } else {
                items = Collections.emptyList();
            }
            return new AnswerList(items);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read AnswerList JSON", e);
        }
    }
}
