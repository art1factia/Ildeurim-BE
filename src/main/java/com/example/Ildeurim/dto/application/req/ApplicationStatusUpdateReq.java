package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

//최종 저장 dto
public record ApplicationStatusUpdateReq(
        @NotNull String newStatus //ApplicationStatus
) {
    public ApplicationStatus toApplicationStatus() {
        return ApplicationStatus.fromString(newStatus); }
}
