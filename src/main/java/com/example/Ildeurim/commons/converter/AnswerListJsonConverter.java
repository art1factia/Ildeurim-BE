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

    private static final ObjectMapper MAPPER = new ObjectMapper();
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
