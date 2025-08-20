package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Job extends BaseEntity {
    @Id // id 필드를 기본키(Primary Key)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private Boolean isWorking; // 현재 근무 여부

    @Column(nullable = false)
    private String jobTitle;   // 직무명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkPlace workPlace; // 근무 장소

    @Column(nullable = true)
    private String contractUrl; // 계약서 파일 URL

    @Column(columnDefinition = "json", nullable = true)
    private String contractCore; // 계약서 요약 JSON

    @OneToOne(mappedBy = "job")
    private Application application;


}
