package com.example.Ildeurim.domain;

import com.example.Ildeurim.commons.domains.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPost extends BaseEntity {
    @Id // id 필드를 기본키(Primary Key)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
}
