package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.commons.enums.worker.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Worker extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;  // 고용인 ID (PK)

    @Column
    private String profileImgURL; // 프로필 이미지 URL

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일

    @Column(nullable = false)
    @NotBlank
    private String name; // 이름

    @Column(nullable = false)
    @NotBlank
    private String phoneNumber; // 연락처

    @Column(nullable = false)
    @NotBlank
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; // 성별

    @Column(nullable = false)
    @NotBlank
    private String residence; // 거주 지역

    @Column(nullable = false)
    private String RLG; //지금은 서율로 고정

    // 희망 근무 지역 (BLG) - 여러 개 선택 가능
    @ElementCollection(targetClass = WorkPlace.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "workerBlgs",
            joinColumns = @JoinColumn(name = "workerId")
    )
    @Column(name = "blg", nullable = false)
    private List<WorkPlace> BLG = new ArrayList<>();

    // 구직 분야 (jobInterest) - 여러 개 선택 가능
    @ElementCollection(targetClass = JobField.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "workerJobInterests",
            joinColumns = @JoinColumn(name = "workerId")
    )
    @Column(name = "jobField", nullable = false)
    private List<JobField> jobInterest = new ArrayList<>();

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Career> careers=new ArrayList<>();

}
