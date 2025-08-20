package com.example.Ildeurim.commons.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.example.Ildeurim.domain.quickAnswer.QuestionList;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class QuestionListJsonConverter implements AttributeConverter<QuestionList, String> {
    private static final ObjectMapper om = new ObjectMapper(); // 필요시 ObjectMapper bean 주입해도 됨
    private static final TypeReference<QuestionList> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(QuestionList attribute) {
        try {
            return (attribute == null) ? null : om.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to write QuestionList JSON", e);
        }
    }

    @Override
    public QuestionList convertToEntityAttribute(String dbData) {
        try {
            return (dbData == null || dbData.isBlank())
                    ? QuestionList.empty()
                    : om.readValue(dbData, TYPE);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read QuestionList JSON", e);
        }
    }
}
