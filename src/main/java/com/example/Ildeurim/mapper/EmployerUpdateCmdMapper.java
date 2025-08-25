package com.example.Ildeurim.mapper;

import com.example.Ildeurim.command.EmployerUpdateCmd;
import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.dto.employer.EmployerUpdateReq;
import org.mapstruct.Mapper;

@Mapper(config = CommonMapperConfig.class, uses = {JobFieldMapper.class})
public interface EmployerUpdateCmdMapper {

    @org.mapstruct.Mapping(target = "name",
            expression = "java(java.util.Optional.ofNullable(src.name()))")
    @org.mapstruct.Mapping(target = "email",
            expression = "java(java.util.Optional.ofNullable(src.email()))")
    @org.mapstruct.Mapping(target = "bossName",
            expression = "java(java.util.Optional.ofNullable(src.bossName()))")
    @org.mapstruct.Mapping(target = "phoneNumber",
            expression = "java(java.util.Optional.ofNullable(src.phoneNumber()))")
    @org.mapstruct.Mapping(target = "companyName",
            expression = "java(java.util.Optional.ofNullable(src.companyName()))")
    @org.mapstruct.Mapping(target = "companyLocation",
            expression = "java(java.util.Optional.ofNullable(src.companyLocation()))")
    @org.mapstruct.Mapping(target = "companyNumber",
            expression = "java(java.util.Optional.ofNullable(src.companyNumber()))")
    @org.mapstruct.Mapping(target = "jobFields",
            expression = "java(java.util.Optional.ofNullable(jobFieldMapper.toJobFieldSet(src.jobFields())))")
    EmployerUpdateCmd toCmd(EmployerUpdateReq src, @org.mapstruct.Context JobFieldMapper jobFieldMapper);
}
