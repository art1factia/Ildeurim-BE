package com.example.Ildeurim.mapper;

import com.example.Ildeurim.command.JobPostUpdateCmd;
import com.example.Ildeurim.commons.CommonMapperConfig;
import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.dto.jobpost.JobPostUpdateReq;
import jakarta.persistence.EntityManager;
import org.hibernate.jdbc.Work;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

@Mapper(
        config = CommonMapperConfig.class,
        uses = {JobFieldMapper.class, ApplyMethodMapper.class, WorkDaysMapper.class, WorkPlaceMapper.class}
)
public interface JobPostUpdateCmdMapper {

    // Optional 래핑 헬퍼
    @Named("opt")
    public static <T> Optional<T> opt(T v) {
        return Optional.ofNullable(v);
    }


    @Mapping(target = "title", qualifiedByName = "opt", source = "title")
    @Mapping(target = "paymentType",
            expression = "java(java.util.Optional.ofNullable( PaymentType.fromLabelNullable(src.paymentType()) ))")
    @Mapping(target = "payment", qualifiedByName = "opt", source = "payment")
    @Mapping(target = "location", qualifiedByName = "opt", source = "location")
    @Mapping(target = "content", qualifiedByName = "opt", source = "content")
    @Mapping(target = "workStartTime", qualifiedByName = "opt", source = "workStartTime")
    @Mapping(target = "workEndTime", qualifiedByName = "opt", source = "workEndTime")
    @Mapping(target = "workType",
            expression = "java(java.util.Optional.ofNullable( WorkType.fromLabelNullable(src.workType()) ))")
    @Mapping(target = "workDays",
            expression = "java(java.util.Optional.ofNullable(workDaysMapper.toWorkDaysSet(src.workDays())))")
    @Mapping(target = "workDaysCount", qualifiedByName = "opt", source = "workDaysCount")
    @Mapping(target = "status",
            expression = "java(java.util.Optional.ofNullable( JobPostStatus.fromLabelNullable(src.status()) ))")
    @Mapping(target = "careerRequirement", qualifiedByName = "opt", source = "careerRequirement")
    @Mapping(target = "educationRequirement",
            expression = "java(java.util.Optional.ofNullable( EducationRequirement.fromLabelNullable(src.educationRequirement()) ))")
    @Mapping(target = "employmentType",
            expression = "java(java.util.Optional.ofNullable( EmploymentType.fromLabelNullable(src.employmentType()) ))")
    @Mapping(target = "jobFields",
            expression = "java(java.util.Optional.ofNullable(jobFieldMapper.toJobFieldMapper(src.jobFields())))")
    @Mapping(target = "applyMethods",
            expression = "java(java.util.Optional.ofNullable(applyMethodMapper.toApplyMethodSet(src.applyMethods())))")
    @Mapping(target = "expiryDate",
            expression = "java(java.util.Optional.ofNullable(src.expiryDate()))")
    @Mapping(target = "workPlace",
            expression = "java(java.util.Optional.ofNullable( WorkPlace.fromLabelNullable(src.workPlace()) ))")
    JobPostUpdateCmd toCmd(JobPostUpdateReq src,
                           @Context JobFieldMapper jobFieldMapper,
                           @Context ApplyMethodMapper applyMethodMapper,
                           @Context WorkDaysMapper workDaysMapper,
                           @Context WorkPlaceMapper workPlaceMapper);
}
