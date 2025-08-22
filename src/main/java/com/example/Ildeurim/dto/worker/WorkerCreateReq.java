package com.example.Ildeurim.dto.worker;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.Gender;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.mapper.WorkPlaceMapper;
import jakarta.validation.constraints.*;   // Bean Validation (Spring Boot 3+)
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public record WorkerCreateReq(
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @NotNull /*@Past*/ LocalDate birthday,   // 날짜 검증을 강화하려면 @Past 권장
        @NotNull Gender gender,
        @NotBlank String residence,
        @NotBlank String RLG,
        @NotNull /*@NotEmpty @Size(min = 1)*/ Set<String> BLG,
        @NotNull /*@NotEmpty @Size(min = 1)*/ Set<String> jobInterest
) {
    public Worker toEntity() {
        return Worker.builder()
                .name(name)
                .birthday(birthday)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .residence(residence)
                .RLG(RLG)
                .BLG(BLG.stream().map(WorkPlace::fromLabel).collect(Collectors.toSet()))
                .jobInterest(jobInterest.stream().map(JobField::fromLabel).collect(Collectors.toSet()))
                .build();
    }
}