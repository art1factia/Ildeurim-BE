package com.example.Ildeurim.commons.converter;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EvaluationAnswerConverter implements AttributeConverter<EvaluationAnswer, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EvaluationAnswer attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public EvaluationAnswer convertToEntityAttribute(Integer dbData) {
        return EvaluationAnswer.fromCode(dbData);
    }
}