package com.example.Ildeurim.mapper;

import com.example.Ildeurim.command.WorkerUpdateCmd;
import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.dto.worker.WorkerUpdateReq;
import org.mapstruct.Mapper;

@Mapper(config = CommonMapperConfig.class, uses = {JobFieldMapper.class, WorkPlaceMapper.class})
public interface WorkerUpdateCmdMapper {
    @org.mapstruct.Named("trimToNull")
    public static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @org.mapstruct.Mapping(target = "name", expression = "java(java.util.Optional.ofNullable(src.name()))")
    @org.mapstruct.Mapping(target = "phoneNumber", expression = "java(java.util.Optional.ofNullable(src.phoneNumber()))")
    @org.mapstruct.Mapping(target = "birthday", expression = "java(java.util.Optional.ofNullable(src.birthday()))")
    @org.mapstruct.Mapping(target = "gender", expression = "java(java.util.Optional.ofNullable(src.gender()))")
    @org.mapstruct.Mapping(target = "residence", expression = "java(java.util.Optional.ofNullable(src.residence()))")
    @org.mapstruct.Mapping(target = "RLG", expression = "java(java.util.Optional.ofNullable(src.RLG()))")
    @org.mapstruct.Mapping(target = "BLG", expression = "java(java.util.Optional.ofNullable(src.BLG()))")
    @org.mapstruct.Mapping(target = "jobInterest", expression = "java(java.util.Optional.ofNullable(src.jobInterest()))")
    WorkerUpdateCmd toCmd(WorkerUpdateReq req, @org.mapstruct.Context JobFieldMapper jobFieldMapper,WorkPlaceMapper workPlaceMapper);
}
