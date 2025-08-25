package com.example.Ildeurim.dto.application.res;

import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.Worker;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Builder
public class ApplicationListRes {
    // 지원서 ID
    private Long applicationId;
    // 지원자 이름
    private String workerName;
    // 지원자 성별
    private String workerGender;
    // 지원자 나이
    private int workerAge;
    // 지원자 거주 지역
    private String workerAddress;
    private WorkPlace workplace;
    // 지원서 상태 (예: '지원 완료', '면접 진행 중')
    private String applicationStatus;



    public static ApplicationListRes of(Application application) {
        Worker worker = application.getWorker();

        //나이 계산
        LocalDate birthDate = worker.getBirthday();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        return ApplicationListRes.builder()
                .applicationId(application.getId())
                .workerName(worker.getName())
                .workerGender(worker.getGender().getLabel())
                .workerAge(age)
                .workerAddress(worker.getResidence())
                .applicationStatus(application.getApplicationStatus().getLabel())
                .build();
    }
}
