package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

//최종 저장 dto
public record ApplicationStatusUpdateReq(
        @NotNull Long applicationId,
        @NotNull ApplicationStatus newStatus
) {}
