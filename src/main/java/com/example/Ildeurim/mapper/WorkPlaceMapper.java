package com.example.Ildeurim.mapper;

import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import org.mapstruct.Mapper;

@Mapper(config = CommonMapperConfig.class)
public interface WorkPlaceMapper {
    default WorkPlace toWorkPlace(String raw) {
        if (raw == null) return null;
        return WorkPlace.fromLabel(raw.trim());
    }

    default java.util.Set<WorkPlace> toWorkPlaceSet(java.util.List<String> list) {
        if (list == null) return null;
        return list.stream()
                .map(this::toWorkPlace)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
