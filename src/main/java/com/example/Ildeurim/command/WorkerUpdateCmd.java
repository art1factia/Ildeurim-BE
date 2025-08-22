package com.example.Ildeurim.command;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.Gender;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Employer;
import jakarta.validation.constraints.*;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record WorkerUpdateCmd(
        Optional<String> name,
        Optional<String> phoneNumber,
        Optional<LocalDate> birthday,
        Optional<Gender> gender,
        Optional<String> residence,
        Optional<String> RLG,
        Optional<Set<WorkPlace>> BLG,
        Optional<Set<JobField>> jobInterest
) {
}
