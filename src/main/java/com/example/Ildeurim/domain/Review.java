package com.example.Ildeurim.domain;
import com.example.Ildeurim.commons.domains.BaseEntity;
import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id // id 필드를 기본키(Primary Key)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="workerId", nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employerId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employer employer;

    @ElementCollection(targetClass = Hashtag.class)
    @CollectionTable(
            name = "reviewHashtags", // 테이블 이름 명확하게 변경
            joinColumns = @JoinColumn(name = "reviewId")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "hashtag", nullable = false)
    private Set<Hashtag> hashtags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "reviewAnswers", joinColumns = @JoinColumn(name = "reviewId"))
    @MapKeyEnumerated(EnumType.STRING)   // question = EvaluationType (문자열 저장)
    @Column(name = "answer")             // answer = int (Converter 덕분에 숫자로 저장)
    @MapKeyColumn(name = "evaluationType")
    private Map<EvaluationType, EvaluationAnswer> answers = new HashMap<>();


}
