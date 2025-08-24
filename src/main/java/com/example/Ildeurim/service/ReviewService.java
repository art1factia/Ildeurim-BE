package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.commons.enums.UserType;
import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.Review;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.review.ReviewCreateReq;
import com.example.Ildeurim.dto.review.ReviewRes;
import com.example.Ildeurim.dto.review.ReviewSummaryRes;
import com.example.Ildeurim.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final WorkerRepository workerRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    //리뷰 생성
    @Transactional
    public ReviewRes createReview(ReviewCreateReq req) {
        // 로그인한 사용자(worker) 확인
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new SecurityException("인증되지 않은 사용자입니다."));

        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new SecurityException("사용자 유형을 알 수 없습니다."));

        // 근로자만 작성 가능
        if (userType != UserType.WORKER) {
            throw new IllegalArgumentException("근로자만 리뷰를 작성할 수 있습니다.");
        }

        // Worker를 조회
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 사용자입니다"));


        // 리뷰 대상 고용주 조회
        Employer employer = employerRepository.findById(req.employerId())
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 고용인입니다"));

        Application application = applicationRepository.findByWorker_IdAndJob_Id(workerId, req.jobId())
                .orElseThrow(() -> new IllegalStateException("해당 고용주의 모집공고에 지원한 이력이 없습니다."));
        // 근무 이력 확인
        boolean hasWorked = jobRepository.existsByWorker_IdAndApplication_Id(workerId, application.getId());
        if (!hasWorked) {
            throw new IllegalStateException("해당 고용주 밑에서 근무한 이력이 없습니다. 리뷰를 작성할 수 없습니다.");
        }

        Map<EvaluationType, EvaluationAnswer> answers = req.toAnswerEnums();
        List<Hashtag> hashtagList = req.toHashtagEnums();
        Set<Hashtag> hashtagSet = new HashSet<>(hashtagList);

        // 리뷰 생성
        Review review = Review.builder()
                .worker(worker)
                .employer(employer)
                .answers(answers)
                .hashtags(hashtagSet)
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
