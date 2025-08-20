package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.jobpost.ApplyMethod;
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
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;        // 지원서 생성일

    @Column(nullable = false)
    private LocalDateTime submissionTime;   // 지원서 제출 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus applicationStatus; // DRAFT / NEED_INTERVIEW / PENDING / ACCEPTED / REJECTED / HIRED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplyMethod applyMethod;        // QUICK / PHONE

//    @ElementCollection
//    @CollectionTable(
//            name = "applicationAnswers",
//            joinColumns = @JoinColumn(name = "applicationId")
//    )
//    private List<Answer> answers = new ArrayList<>(); // 질문-답변 리스트

    @Column(nullable = false)
    private Boolean isCareerIncluding=false;     // 이력서 포함 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPostId", nullable = false)
    private JobPost jobPost;                // 어떤 모집 공고에 대한 지원서인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workerId", nullable = false)
    private Worker worker; // 지원자 정보


    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Job job;

//    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
//    private Application application;

//    @Embeddable
//    @Getter
//    @Setter
//    public static class Answer {
//        private String question;
//        private String answer;
//    }
}
