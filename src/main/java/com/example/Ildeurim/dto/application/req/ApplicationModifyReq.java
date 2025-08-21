package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;


public record ApplicationModifyReq(
        Long applicationId,
        Boolean isCareerIncluding,
        AnswerList answers
) {}