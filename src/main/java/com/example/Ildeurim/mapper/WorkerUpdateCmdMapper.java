package com.example.Ildeurim.mapper;

import com.example.Ildeurim.command.WorkerUpdateCmd;
import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.dto.worker.WorkerUpdateReq;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = CommonMapperConfig.class, uses = {JobFieldMapper.class, WorkPlaceMapper.class, DateMapper.class})
public interface WorkerUpdateCmdMapper {
    @Named("trimToNull")
    public static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @Mapping(target = "name",
            expression = "java(java.util.Optional.ofNullable(WorkerUpdateCmdMapper.trimToNull(src.name())))")
    @Mapping(target = "phoneNumber",
            expression = "java(java.util.Optional.ofNullable(WorkerUpdateCmdMapper.trimToNull(src.phoneNumber())))")
    @Mapping(target = "birthday", qualifiedByName = "optLocalDate", source = "birthday")
    @Mapping(target = "gender",
            expression = "java(java.util.Optional.ofNullable( Gender.fromLabelNullable(src.gender()) ))")
    @Mapping(target = "residence",
            expression = "java(java.util.Optional.ofNullable(WorkerUpdateCmdMapper.trimToNull(src.residence())))")
    @Mapping(target = "RLG",
            expression = "java(java.util.Optional.ofNullable(WorkerUpdateCmdMapper.trimToNull(src.RLG())))")
    @Mapping(target = "BLG",
            expression = "java(java.util.Optional.ofNullable(workPlaceMapper.toWorkPlaceSet( src.BLG() )))")
    @Mapping(target = "jobInterest",
            expression = "java(java.util.Optional.ofNullable(jobFieldMapper.toJobFieldSet( src.jobInterest())))")
    WorkerUpdateCmd toCmd(WorkerUpdateReq src, @Context JobFieldMapper jobFieldMapper, @Context WorkPlaceMapper workPlaceMapper, @Context DateMapper dateMapper);
}
