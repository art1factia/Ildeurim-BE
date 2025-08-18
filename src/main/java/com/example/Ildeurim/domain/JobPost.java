package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.jobpost.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class JobPost extends BaseEntity {   // ✅ BaseEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;                  // 모집 제목

    @Column(nullable = false, columnDefinition = "TEXT",length = 500)
    private String content;                // 상세 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;       // 급여 유형

    @Column(nullable = false)
    private Long payment;                  // 급여 금액

    @Column(nullable = false)
    private String location;               // 근무 위치

    @Column(nullable = false)
    private Integer restime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    @ElementCollection
    @CollectionTable(
            name = "jobpostWorkDays",
            joinColumns = @JoinColumn(name = "jobpostId")
    )
    @Enumerated(EnumType.STRING)
    private List<WorkDays> workDays;

    private Integer workNumber;

    @ElementCollection
    @CollectionTable(
            name = "jobpostApplyMethods",
            joinColumns = @JoinColumn(name = "jobpostId")
    )
    @Enumerated(EnumType.STRING)
    private List<ApplyMethod> applyMethods; // 지원 방법 (간편지원, 전화, 이메일 등)

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

    @Column(nullable = false)
    private Boolean haveCareer= false; // 경력 요구사항

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationRequirement educationRequirement; // 학력 요구사항

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType; // 고용 형태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employerId", nullable = false)
    private Employer employer;             // 고용주 정보

    @OneToMany(mappedBy = "jobPost", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications; // 지원자 리스트
}
