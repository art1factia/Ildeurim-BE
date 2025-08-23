package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

//최종 저장 dto
public record ApplicationStatusUpdateReq(
        @NotNull String newStatus //ApplicationStatus
) {
    public ApplicationStatus toApplicationStatus() {
        try {
            return ApplicationStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 지원서 상태입니다: " + newStatus);
        }
    }
}
