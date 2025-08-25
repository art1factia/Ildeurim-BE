package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.converter.QuestionListJsonConverter;
import com.example.Ildeurim.command.EmployerUpdateCmd;
import com.example.Ildeurim.command.WorkerUpdateCmd;
import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.domain.quickAnswer.QuestionList;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 안전하게 생성자 보호
@AllArgsConstructor
public class Employer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String bossName;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$")
    private String phoneNumber;

    @Column(nullable = false)
    @NotBlank
    private String companyName;

    @Column(nullable = false)
    @NotBlank
    private String companyLocation;

    @Column(nullable = false, unique = true)
    @Pattern(
            regexp = "^\\d{3}-\\d{2}-\\d{5}$",
            message = "사업자번호 형식이 올바르지 않습니다. 예: 123-45-67890"
    )
    private String companyNumber;

    @ElementCollection(targetClass = JobField.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "employerJobField",
            joinColumns = @JoinColumn(name = "employerId")
    ) //구직 분야 여러개 선택 가능
    @Column(name = "jobField")
    private Set<JobField> jobFields = new HashSet<>();

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobPost> jobPosts = new ArrayList<>();

    @Convert(converter = QuestionListJsonConverter.class)
  @Column(columnDefinition = "jsonb") // PostgreSQL이면 jsonb 추천
//    @Column(columnDefinition = "text")
    private QuestionList defaultQuestionList; // 기본 질문 세트

    @OneToMany(mappedBy = "employer")
    private List<Review> reviews = new ArrayList<>();

    public void update(EmployerUpdateCmd cmd) {
        cmd.name().ifPresent(this::setName);
        cmd.email().ifPresent(this::setEmail);
        cmd.bossName().ifPresent(this::setBossName);
        cmd.phoneNumber().ifPresent(this::setPhoneNumber);
        cmd.companyName().ifPresent(this::setCompanyName);
        cmd.companyLocation().ifPresent(this::setCompanyLocation);
        cmd.companyNumber().ifPresent(this::setCompanyNumber);
        cmd.jobFields().ifPresent(this::setJobFields);
    }
}