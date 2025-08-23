package com.example.Ildeurim.mapper;

import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import org.mapstruct.Mapper;

// 문자열을 Hashtag enum으로 바꾸는 매퍼
@Mapper(config = CommonMapperConfig.class)
public interface HashtagMapper {
    default Hashtag toHashtag(String raw) {
        if (raw == null) return null;
        return Hashtag.fromLabel(raw.trim());
    }

    default java.util.Set<Hashtag> toHashtagSet(java.util.List<String> list) {
        if (list == null) return null;
        return list.stream()
                .map(this::toHashtag)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
