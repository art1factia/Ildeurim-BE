package com.example.Ildeurim.mapper;

import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.commons.converter.DateParsers;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(config = CommonMapperConfig.class)
public interface DateMapper {

    @Named("toLocalDateOrNull")
    default LocalDate toLocalDateOrNull(String s) {
        return DateParsers.parseLocalDate(s);
    }

    @Named("toLocalDateTimeOrNull")
    default LocalDateTime toLocalDateTimeOrNull(String s) {
        return DateParsers.parseLocalDateTime(s);
    }

    @Named("optLocalDate")
    default java.util.Optional<LocalDate> optLocalDate(String s) {
        return java.util.Optional.ofNullable(toLocalDateOrNull(s));
    }

    @Named("optLocalDateTime")
    default java.util.Optional<LocalDateTime> optLocalDateTime(String s) {
        return java.util.Optional.ofNullable(toLocalDateTimeOrNull(s));
    }
}
