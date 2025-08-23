package com.example.Ildeurim.dto.career;

import com.example.Ildeurim.domain.Career;

public record CareerRes(

) {
    public static CareerRes from(Career career) {
        //TODO: career -> careerRes 매핑 메서드 from 작성
        return new CareerRes();
    }
}
