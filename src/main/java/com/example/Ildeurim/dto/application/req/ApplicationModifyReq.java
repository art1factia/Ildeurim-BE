package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record ApplicationModifyReq(
        @NotNull Boolean isCareerIncluding,
        @NotNull AnswerList/*List<List<String>>*/ answers  //AnswerList
) {}