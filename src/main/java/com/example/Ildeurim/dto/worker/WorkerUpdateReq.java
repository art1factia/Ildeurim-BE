package com.example.Ildeurim.dto.worker;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.Gender;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;

import java.time.LocalDate;
import java.util.Set;

public record WorkerUpdateReq(
        String name,
        String phoneNumber,
        String birthday,
        Gender gender,
        String residence,
        String RLG,
        Set<String> BLG,
        Set<String> jobInterest
) {
}
