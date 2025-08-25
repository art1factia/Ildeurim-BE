package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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


    @Column(name = "contractCore", columnDefinition = "text", nullable = false)
    private String contractCore = "{}";

    // 편의 메서드 (선택)
    public void setContractCoreFromJson(JsonNode node,
                                    ObjectMapper om) {
        try { this.contractCore = om.writeValueAsString(node == null ? om.createObjectNode() : node); }
        catch (Exception e) { throw new IllegalArgumentException("contractCore serialize failed", e); }
    }
    public JsonNode getContractCoreAsJson(ObjectMapper om) {
        try { return om.readTree(this.contractCore == null ? "{}" : this.contractCore); }
        catch (Exception e) { return om.createObjectNode(); }
    }


    // Job.java (owning side)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicationId", nullable = false, unique = true) // ★ unique로 1:1 보장
    private Application application;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="workerId", nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Worker worker;


}
