package com.example.umc_study.global.handler;

import com.example.umc_study.domain.review.dto.ReviewReqDTO;
import com.example.umc_study.domain.review.exception.code.ReviewErrorCode;
import com.example.umc_study.global.apiPayload.ApiResponse;
import com.example.umc_study.global.code.BaseErrorCode;
import com.example.umc_study.global.code.GeneralErrorCode;
import com.example.umc_study.global.exception.ProjectException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionAdvice {

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<ApiResponse<Void>> handleProjectException(ProjectException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        BaseErrorCode errorCode = resolveValidationErrorCode(ex);
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(null);

        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode, message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(
            ConstraintViolationException ex
    ) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(code, ex.getMessage()));
    }

    private BaseErrorCode resolveValidationErrorCode(MethodArgumentNotValidException ex) {
        Object target = ex.getBindingResult().getTarget();
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);

        if (target instanceof ReviewReqDTO.CreateReviewDTO && fieldError != null) {
            return switch (fieldError.getField()) {
                case "memberId" -> ReviewErrorCode.REVIEW_MEMBER_ID_REQUIRED;
                case "score" -> ReviewErrorCode.REVIEW_SCORE_INVALID;
                case "body" -> ReviewErrorCode.REVIEW_BODY_REQUIRED;
                default -> GeneralErrorCode.BAD_REQUEST;
            };
        }

        return GeneralErrorCode.BAD_REQUEST;
    }
}
