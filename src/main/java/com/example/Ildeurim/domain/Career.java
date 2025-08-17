package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.JobField;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;   // 시작일

    @Column(nullable = false)
    private LocalDate endDate;     // 종료일

    @Column(nullable = false)
    @NotBlank
    private String title;          // 경력 제목

    @Column(nullable = false, length = 500)
    private String mainDuties;    // 근무 내용

    @Column(nullable = false)
    private String companyName;    // 회사 이름

    @Column(nullable = false)
    private String location;       // 근무 위치

    @Column(nullable = false)
    private Boolean isOpening = false; // 공개 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobField jobField;     // 직무 분야

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workerId", nullable = false)
    private Worker worker;
  
}
