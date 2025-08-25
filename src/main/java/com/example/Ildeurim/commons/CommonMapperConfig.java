package com.example.Ildeurim.commons;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@MapperConfig(
        componentModel = "spring", // 스프링 빈으로 주입받기
        unmappedTargetPolicy = ReportingPolicy.ERROR, // 매핑 누락 시 컴파일 에러
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE // null → 패치에서 무시
)
public interface CommonMapperConfig {}