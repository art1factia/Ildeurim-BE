package com.example.Ildeurim.service.review;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.Review;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.review.ReviewCreateReq;
import com.example.Ildeurim.dto.review.ReviewRes;
import com.example.Ildeurim.dto.review.ReviewSummaryRes;
import com.example.Ildeurim.repository.EmployerRepository;
import com.example.Ildeurim.repository.JobRepository;
import com.example.Ildeurim.repository.ReviewRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final WorkerRepository workerRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;

    //리뷰 생성
    @Transactional
    public ReviewRes createReview(Long workerId, ReviewCreateReq req) {
        // 로그인한 사용자(worker) 조회
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사용자입니다"));

        // 리뷰 대상 고용주 조회
        Employer employer = employerRepository.findById(req.employerId())
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 고용인입니다"));

        // 근무 이력 확인
        boolean hasWorked = jobRepository.existsByWorkerAndEmployer(workerId, employer.getId());
        if (!hasWorked) {
            throw new IllegalStateException("해당 고용주 밑에서 근무한 이력이 없습니다. 리뷰를 작성할 수 없습니다.");
        }

        // 리뷰 생성
        Review review = Review.builder()
                .worker(worker)
                .employer(employer)
                .answers(req.answers())
                .hashtags(req.hashtags())
                .build();

        // 저장
        reviewRepository.save(review);

        return ReviewRes.of(review);
    }

    /*--------------------------------------------------------------*/

    //리뷰 조회
    @Transactional
    public ReviewSummaryRes getReview(Long employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new IllegalArgumentException("Employer not found"));

        List<Review> reviews = reviewRepository.findByEmployer(employer);

        if (reviews.isEmpty()) {
            return ReviewSummaryRes.builder()
                    .employerId(employerId)
                    .averages(Map.of())
                    .hashtagCounts(Map.of())
                    .totalReviews(0L)
                    .build();
        }

        // 질문별 평균 계산
        Map<EvaluationType, Double> avgScores = new HashMap<>();
        for (EvaluationType type : EvaluationType.values()) {
            double avg = reviews.stream()
                    .map(r -> r.getAnswers().get(type))
                    .filter(Objects::nonNull)
                    .mapToInt(EvaluationAnswer::getCode)
                    .average()
                    .orElse(0.0);

            avgScores.put(type, avg);
        }

        // 평균값을 GOOD / NORMAL / BAD 로 매핑
        Map<EvaluationType, EvaluationAnswer> avgAnswers = avgScores.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> EvaluationAnswer.fromAverage(e.getValue())
                ));

        // 해시태그 빈도 계산
        Map<Hashtag, Long> hashtagCounts = reviews.stream()
                .flatMap(r -> r.getHashtags().stream())
                .collect(Collectors.groupingBy(h -> h, Collectors.counting()));

        return ReviewSummaryRes.builder()
                .employerId(employerId)
                .averages(avgAnswers)
                .hashtagCounts(hashtagCounts)
                .totalReviews((long) reviews.size())
                .build();
    }

    //
}
