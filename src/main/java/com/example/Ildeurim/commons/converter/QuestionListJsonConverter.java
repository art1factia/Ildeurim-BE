package com.example.Ildeurim.commons.converter;

import com.example.Ildeurim.domain.quickAnswer.QuestionItem;
import com.example.Ildeurim.domain.quickAnswer.QuestionList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

@Converter(autoApply = true)
public class QuestionListJsonConverter implements AttributeConverter<QuestionList, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<QuestionItem>> LIST_TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(QuestionList attribute) {
        try {
            // 항상 배열(JSON array)로 저장
            if (attribute == null || attribute.items() == null) {
                return "[]";
            }
            return MAPPER.writeValueAsString(attribute.items());
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
            JsonNode node = MAPPER.readTree(dbData);

            List<QuestionItem> items;
            if (node.isArray()) {
                items = MAPPER.convertValue(node, LIST_TYPE);
            } else if (node.isObject() && node.has("items") && node.get("items").isArray()) {
                // 구형 포맷: {"items":[...]}
                items = MAPPER.convertValue(node.get("items"), LIST_TYPE);
            } else {
                // 알 수 없는 포맷은 빈 배열로
                items = Collections.emptyList();
            }
            return new QuestionList(items);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read QuestionList JSON", e);
        }
    }
}
