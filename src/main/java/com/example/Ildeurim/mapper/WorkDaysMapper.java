package com.example.Ildeurim.mapper;

import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.commons.enums.jobpost.WorkDays;
import org.mapstruct.Mapper;

@Mapper(config = CommonMapperConfig.class)
public interface WorkDaysMapper {
    default WorkDays toWorkDays(String raw) {
        if (raw == null) return null;
        return WorkDays.fromLabel(raw.trim());
    }
    default java.util.Set<WorkDays> toWorkDaysSet(java.util.List<String> list) {
        if (list == null) return null;
        return list.stream()
                .map(this::toWorkDays)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}