package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.converter.QuestionListJsonConverter;
import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.domain.quickAnswer.QuestionList;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class JobPost extends BaseEntity {

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
    private Integer restTime; //오타 수정 (restime -> restTime)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobField jobField;

    @ElementCollection
    @CollectionTable(
            name = "jobPostWorkDays",
            joinColumns = @JoinColumn(name = "jobPostId")
    )
    @Enumerated(EnumType.STRING)
    private List<WorkDays> workDays;

    private Integer workDaysCount;

    @Column(nullable = false)
    private Boolean careerRequirement; //type이 boolean이라 enum 없이 사용하도록 변경

    @ElementCollection
    @CollectionTable(
            name = "jobPostApplyMethods",
            joinColumns = @JoinColumn(name = "jobPostId")
    )
    @Enumerated(EnumType.STRING)
    private List<ApplyMethod> applyMethods; // 지원 방법 (간편지원, 전화, 이메일 등)

    @Column(nullable = false)
    private LocalDateTime startDate;       // 모집 시작일

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private List<ApplyMethod> applyMethod;

    @Column(nullable = false)
    private Boolean isJobPostUsing=false;  //모집 공고를 계속 이용할건지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employerId", nullable = false)
    private Employer employer;             // 고용주 정보

    @OneToMany(mappedBy = "jobPost", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications; // 지원자 리스트

    @Convert(converter = QuestionListJsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private QuestionList questionList;
}
