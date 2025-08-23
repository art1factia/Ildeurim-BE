package com.example.Ildeurim.mapper;

import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.commons.enums.jobpost.ApplyMethod;
import org.mapstruct.Mapper;

@Mapper(config = CommonMapperConfig.class)
public interface ApplyMethodMapper {
    default ApplyMethod toApplyMethod(String raw) {
        if (raw == null) return null;
        return ApplyMethod.fromLabel(raw.trim());
    }
    default java.util.Set<ApplyMethod> toApplyMethodSet(java.util.List<String> list) {
        if (list == null) return null;
        return list.stream()
                .map(this::toApplyMethod)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
