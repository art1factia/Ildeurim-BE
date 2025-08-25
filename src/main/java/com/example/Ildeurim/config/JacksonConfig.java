package com.example.Ildeurim.config;

// JacksonConfig.java

import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.gpt.LabelEnumDeserializer;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule m = new SimpleModule()
                .addDeserializer(JobField.class,         new LabelEnumDeserializer<>(JobField.class))
                .addDeserializer(WorkPlace.class,        new LabelEnumDeserializer<>(WorkPlace.class))
                .addDeserializer(WorkType.class,         new LabelEnumDeserializer<>(WorkType.class))
                .addDeserializer(WorkDays.class,         new LabelEnumDeserializer<>(WorkDays.class))
                .addDeserializer(PaymentType.class,      new LabelEnumDeserializer<>(PaymentType.class))
                .addDeserializer(EmploymentType.class,   new LabelEnumDeserializer<>(EmploymentType.class))
                .addDeserializer(EducationRequirement.class, new LabelEnumDeserializer<>(EducationRequirement.class))
                .addDeserializer(ApplyMethod.class,      new LabelEnumDeserializer<>(ApplyMethod.class));

        om.registerModule(m)
                .registerModule(new JavaTimeModule())                 // ★ LocalDateTime 지원
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return om;
    }
}
