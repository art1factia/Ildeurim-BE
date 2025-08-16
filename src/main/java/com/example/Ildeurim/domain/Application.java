package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.enums.ApplicationStatus;
import com.example.Ildeurim.commons.enums.ApplyMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;        // 지원서 생성일

    @Column(nullable = false)
    private LocalDateTime registrationTime; // 지원서 제출 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;       // ACCEPTED / REJECTED / PENDING

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplyMethod applyMethod;        // QUICK / PHONE

    private String memo;                     // 고용주 메모

    @Column(columnDefinition = "json")
    private String answersForQuickApplication; // 간편지원 시 답변 JSON 형태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPostId", nullable = false)
    private JobPost jobPost;                 // 어떤 모집 공고에 대한 지원서인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workerId", nullable = false)
    private Worker worker;                   // 지원자 정보



}