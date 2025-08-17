package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.enums.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
//엔티티의 생성 및 수정 시간을 자동으로 감시하고 기록하기 위해 애너테이션 추가
@Getter
@Entity
@NoArgsConstructor
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;       // 생성일

    @Column(nullable = false)
    private LocalDateTime updatedAt;       // 수정일

    @Column(nullable = false)
    private String title;                  // 모집 제목

    @Column(nullable = false)
    private String content;                // 상세 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;       // 급여 유형

    @Column(nullable = false)
    private Long payment;                  // 급여 금액

    @Column(nullable = false)
    private String location;               // 근무 위치

    @Column(nullable = false)
    private String info;                   // 추가 정보 (근무복, 식사 등)

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ApplyMethod.class)
    private List<ApplyMethod> applyMethods; // 지원 방법 (간편지원, 전화, 이메일 등)

    @ElementCollection
    @Column(nullable = false)
    private List<String> questionList;      // 지원 시 질문 목록

    @Column(nullable = false)
    private LocalDateTime expiryDate;      // 마감일

    @Enumerated(EnumType.STRING) //Enum 이름 그대로 문자열로 저장
    @Column(nullable = false)
    private JobPostStatus status;          // OPEN / CLOSED

    @Column(nullable = false)
    private LocalTime workStartTime; //근무 시작 시간

    @Column(nullable = false)
    private LocalTime workEndTime; // 근무 마감 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareerRequirement careerRequirement; //경력

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EducationRequirement educationRequirement; // 학력

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType; // 고용 형태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employerId", nullable = false) //employer_id 컬럼 값은 연결된 Employer 엔티티의 id 값과 같아야함
    private Employer employer;           // 고용주 정보

    @OneToMany(mappedBy = "jobPost",fetch = FetchType.LAZY, cascade = CascadeType.ALL) //jobpost저장시(삭제시) application도 저장(삭제)
    private List<Application> applications; // 지원자 리스트


}