package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.converter.AnswerListJsonConverter;
import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.jobpost.ApplyMethod;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime submissionTime;   // 지원서 제출 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus applicationStatus; // DRAFT / NEED_INTERVIEW / PENDING / ACCEPTED / REJECTED / HIRED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplyMethod applyMethod;        // QUICK / PHONE

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

    @Convert(converter = AnswerListJsonConverter.class)
//    @Column(columnDefinition = "jsonb")
    @Column(columnDefinition = "text")
    private AnswerList answers;

    public void submit() {
        if (this.applicationStatus != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("이미 제출된 지원서입니다.");
        }
        this.applicationStatus = ApplicationStatus.NEEDINTERVIEW;
        this.submissionTime = LocalDateTime.now();
    }

    public void updateStatus(ApplicationStatus newStatus) {
        this.applicationStatus = newStatus;
    }
}
