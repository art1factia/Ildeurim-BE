package com.example.Ildeurim.commons.converter;

import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AnswerListJsonConverter implements AttributeConverter<AnswerList, String> {
    private static final ObjectMapper om = new ObjectMapper();
    private static final TypeReference<AnswerList> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(AnswerList attribute) {
        try {
            return (attribute == null) ? null : om.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to write AnswerList JSON", e);
        }
    }

    @Override
    public AnswerList convertToEntityAttribute(String dbData) {
        try {
            return (dbData == null || dbData.isBlank())
                    ? AnswerList.empty()
                    : om.readValue(dbData, TYPE);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read AnswerList JSON", e);
        }
    }
}
