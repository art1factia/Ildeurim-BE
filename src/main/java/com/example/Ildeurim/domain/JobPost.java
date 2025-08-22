package com.example.Ildeurim.domain;

import com.example.Ildeurim.command.JobPostUpdateCmd;
import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class JobPost extends BaseEntity {   // ✅ BaseEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;                  // 모집 제목

    @Column(nullable = false, columnDefinition = "TEXT", length = 500)
    private String content;                // 상세 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;       // 급여 유형

    @Column(nullable = false)
    private Long payment;                  // 급여 금액

    @Column(nullable = false)
    private String location;               // 근무 위치

    @Column(nullable = false)
    private Integer restTime; //오타 수정 (restime -> restTime)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    @ElementCollection
    @CollectionTable(
            name = "jobPostWorkDays", //오타 수정(jobpostWorkDays -> jobPostWorkDays)
            joinColumns = @JoinColumn(name = "jobPostId") //오타수정((jobpostId -> jobPostId)
    )
    @Enumerated(EnumType.STRING)
    private Set<WorkDays> workDays;

    private Integer workDaysCount;

    @Column(nullable = false)
    private Boolean careerRequirement; //type이 boolean이라 enum 없이 사용하도록 변경

    @ElementCollection
    @CollectionTable(
            name = "jobPostApplyMethods",
            joinColumns = @JoinColumn(name = "jobPostId")
    )
    @Enumerated(EnumType.STRING)
    private Set<ApplyMethod> applyMethods; // 지원 방법 (간편지원, 전화, 이메일 등)

    @ElementCollection
    @CollectionTable(
            name = "jobPostJobFields",
            joinColumns = @JoinColumn(name = "jobPostId")
    )
    @Enumerated(EnumType.STRING)
    private Set<JobField> jobFields;

//    @ElementCollection
//    @CollectionTable(
//            name = "jobpost_questions",
//            joinColumns = @JoinColumn(name = "jobpost_id")
//    )
//    @Column(name = "question", nullable = false)
//    private List<String> questionList;      // 지원 시 질문 목록

    @Column(nullable = false)
    private LocalDateTime startDate;       // 모집 시작일

    @Column(nullable = false)
    private LocalDateTime expiryDate;      // 마감일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobPostStatus status;          // OPEN / CLOSED

    @Column(nullable = false)
    private LocalTime workStartTime;       // 근무 시작 시간

    @Column(nullable = false)
    private LocalTime workEndTime;         // 근무 종료 시간

//    @Column(nullable = false)
//    private Boolean haveCareer= false; // 경력 요구사항

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationRequirement educationRequirement; // 학력 요구사항

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType; // 고용 형태

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkPlace workPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employerId", nullable = false)
    private Employer employer;             // 고용주 정보

    @OneToMany(mappedBy = "jobPost", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications; // 지원자 리스트

    //TODO: update 메서드 만들기
    public void update(JobPostUpdateCmd cmd) {
        cmd.title().ifPresent(this::setTitle);
        cmd.paymentType().ifPresent(this::setPaymentType);
        cmd.payment().ifPresent(this::setPayment);
        cmd.location().ifPresent(this::setLocation);
        cmd.content().ifPresent(this::setContent);
        cmd.workStartTime().ifPresent(this::setWorkStartTime);
        cmd.workEndTime().ifPresent(this::setWorkEndTime);
        cmd.workType().ifPresent(this::setWorkType);
        cmd.workDays().ifPresent(this::setWorkDays);
        cmd.workDaysCount().ifPresent(this::setWorkDaysCount);
        cmd.status().ifPresent(this::setStatus);
        cmd.careerRequirement().ifPresent(this::setCareerRequirement);
        cmd.educationRequirement().ifPresent(this::setEducationRequirement);
        cmd.employmentType().ifPresent(this::setEmploymentType);
        cmd.jobFields().ifPresent(this::setJobFields);
        cmd.applyMethods().ifPresent(this::setApplyMethods);
        cmd.expiryDate().ifPresent(this::setExpiryDate);
        cmd.workPlace().ifPresent(this::setWorkPlace);
    }
}
