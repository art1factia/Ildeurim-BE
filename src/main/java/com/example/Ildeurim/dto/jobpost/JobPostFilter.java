package com.example.Ildeurim.dto.jobpost;

// JobPostFilterDto.java
import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.commons.enums.worker.WorkPlace; // ← 추가

import java.util.Set;

public class JobPostFilter {
    public Set<JobField> jobField;
    public Set<PaymentType> paymentType;
    public Set<WorkDays> workDays;
    public WorkType workType;
    public Integer workDaysCountMin;
    public Integer workDaysCountMax;
    public Long paymentMin;
    public Long paymentMax;
    public Set<EmploymentType> employmentType;
    public Set<ApplyMethod> applyMethods;

    public Set<WorkPlace> workPlace;         // ← 추가 (enum 강제)
    public Set<String> locationContains;     // LIKE 검색(지명 문자열)
    public Set<String> keywords;
}

