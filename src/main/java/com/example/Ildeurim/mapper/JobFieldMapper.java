package com.example.Ildeurim.mapper;


import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.commons.enums.jobpost.JobField;
import org.mapstruct.Mapper;

@Mapper(config = CommonMapperConfig.class)
public interface JobFieldMapper {
    default JobField toJobField(String raw) {
        if (raw == null) return null;
        return JobField.fromLabel(raw.trim());
    }

    default java.util.Set<JobField> toJobFieldSet(java.util.List<String> list) {
        if (list == null) return null;
        return list.stream()
                .map(this::toJobField)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
