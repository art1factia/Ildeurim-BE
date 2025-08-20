package com.example.Ildeurim.dto.worker;


import com.example.Ildeurim.commons.enums.worker.Gender;
import com.example.Ildeurim.domain.Worker;
// import com.example.Ildeurim.commons.enums.Gender;
import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.*;

import java.time.LocalDate;
import java.util.List;

public record WorkerDetailRes(
        Long id,
        String profileImgURL,
        String name,
        String phoneNumber,
        LocalDate birthday,
        Gender gender,
        String residence,
        String RLG,
        List<WorkPlace> BLG,
        List<JobField> jobInterest
) {
    public static WorkerDetailRes from(Worker w) {
        return new WorkerDetailRes(
                w.getId(),
                w.getProfileImgURL(),
                w.getName(),
                w.getPhoneNumber(),
                w.getBirthday(),
                w.getGender(),
                w.getResidence(),
                w.getRLG(),
                w.getBLG() != null ? List.copyOf(w.getBLG()) : List.of(),
                w.getJobInterest() != null ? List.copyOf(w.getJobInterest()) : List.of()
        );
    }
}
