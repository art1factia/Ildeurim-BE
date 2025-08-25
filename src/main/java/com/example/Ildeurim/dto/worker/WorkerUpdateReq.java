package com.example.Ildeurim.dto.worker;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.Gender;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record WorkerUpdateReq(
        String name,
        String phoneNumber,
        String birthday,
        String gender,
        String residence,
        String RLG,
        List<String> BLG,
        List<String> jobInterest
) {
}
