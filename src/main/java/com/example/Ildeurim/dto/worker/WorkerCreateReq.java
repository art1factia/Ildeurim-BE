package com.example.Ildeurim.dto.worker;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.Gender;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Worker;
import jakarta.validation.constraints.*;   // Bean Validation (Spring Boot 3+)
import java.time.LocalDate;
import java.util.List;


public record WorkerCreateReq(
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @NotNull /*@Past*/ LocalDate birthday,   // 날짜 검증을 강화하려면 @Past 권장
        @NotNull Gender gender,
        @NotBlank String residence,
        @NotBlank String RLG,
        @NotNull /*@NotEmpty @Size(min = 1)*/ List<WorkPlace> BLG,
        @NotNull /*@NotEmpty @Size(min = 1)*/ List<JobField> jobInterest
) {
    public Worker toEntity() {
        return Worker.builder()
                .name(name)
                .birthday(birthday)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .residence(residence)
                .RLG(RLG)
                .BLG(BLG)
                .jobInterest(jobInterest)
                .build();
    }
}