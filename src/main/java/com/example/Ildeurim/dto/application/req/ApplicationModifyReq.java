package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record ApplicationModifyReq(
        @NotNull Long applicationId,
        @NotNull Boolean isCareerIncluding,
        @NotNull AnswerList answers
) {}