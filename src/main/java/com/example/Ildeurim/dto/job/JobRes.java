package com.example.Ildeurim.dto.job;

import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Job;

/**
 * Job 응답 DTO
 */
public record JobRes(
        Long id,
        Long workerId,
        Boolean isWorking,
        String jobTitle,
        WorkPlace workPlace,
        String contractUrl,
        String contractCore
) {
    public static JobRes of(Job j) {
        return new JobRes(
                j.getId(),
                j.getWorker() != null ? j.getWorker().getId() : null,
                j.getIsWorking(),
                j.getJobTitle(),
                j.getWorkPlace(),
                j.getContractUrl(),
                j.getContractCore()
        );
    }
}
