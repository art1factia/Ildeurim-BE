package com.example.Ildeurim.exception;

import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.exception.job.JobNotFoundException;
import com.example.Ildeurim.exception.job.JobPermissionException;
import com.example.Ildeurim.exception.application.DuplicateApplicationException;
import com.example.Ildeurim.exception.application.JobPostClosedException;
import com.example.Ildeurim.exception.career.InvalidDateRangeException;
import com.example.Ildeurim.exception.jobPost.JobPostNotFoundException;
import com.example.Ildeurim.exception.jobPost.JobPostPermissionException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 접근 권한이 없는 경우 (인증/인가 실패)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    //공고 수정 권한 없음.
    @ExceptionHandler(JobPostPermissionException.class)
    public ResponseEntity<ApiResponse<?>> handleJobPostPermission(JobPostPermissionException e) {
        log.error("JobPostPermissionException 발생 - 공고 ID: {}", e.getJobPostId());
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    //근로 수정 권한 없음
    @ExceptionHandler(JobPermissionException.class)
    public ResponseEntity<ApiResponse<?>> handleJobNotFound(JobPermissionException e) {
        log.error("JobPermissionException 발생 - 근로 ID: {}", e.getJobId());
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    // 엔티티(리소스)를 찾을 수 없는 경우 (DB 조회 실패 등)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFound(EntityNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    //모집공고 찾을 수 없음.
    @ExceptionHandler(JobPostNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleJobPostNotFound(JobPostNotFoundException e) {
        log.error("JobPostNotFoundException 발생 - 공고 ID: {}", e.getJobPostId());
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    //근로를 찾을 수 없음
    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleJobNotFound(JobNotFoundException e) {
        log.error("JobNotFoundException 발생 - 근로 ID: {}", e.getJobId());
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 잘못된 입력값, 유효하지 않은 요청 파라미터
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    //career 날짜 입력
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidDateRange(InvalidDateRangeException e) {
        log.error("InvalidDateRangeException 발생 - Start: {}, End: {}", e.getStartDate(), e.getEndDate());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 현재 상태에서 수행할 수 없는 동작 (중복 지원, 제출 불가 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalState(IllegalStateException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    //마감된 모집공고 처리
    @ExceptionHandler(JobPostClosedException.class)
    public ResponseEntity<ApiResponse<?>> handleJobPostClosed(JobPostClosedException e) {
        log.error("JobPostClosedException 발생 - 공고 ID: {}", e.getJobPostId());
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    //지원서 중복
    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateApplication(DuplicateApplicationException e) {
        log.error("DuplicateApplicationException 발생 - 사용자 ID: {}, 공고 ID: {}",
                e.getUserId(), e.getJobPostId());
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    // 그 외 모든 예외 (예상치 못한 서버 오류)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    // 공통 에러 응답 생성
    private ResponseEntity<ApiResponse<?>> buildErrorResponse(HttpStatus status, String message) {
        ApiResponse<?> response = new ApiResponse<>(false, status.value(), message);
        return ResponseEntity.status(status).body(response);
    }
}
