package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;

//최종 저장 dto
public record ApplicationStatusUpdateReq(
        Long applicationId,
        ApplicationStatus newStatus
) {}
