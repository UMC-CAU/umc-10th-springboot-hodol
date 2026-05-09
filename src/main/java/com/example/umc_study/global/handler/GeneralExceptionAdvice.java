package com.example.umc_study.global.handler;

import com.example.umc_study.domain.member.dto.MemberReqDTO;
import com.example.umc_study.domain.member.exception.code.MemberErrorCode;
import com.example.umc_study.domain.mission.dto.MissionReqDTO;
import com.example.umc_study.domain.mission.exception.code.MissionErrorCode;
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

        if (fieldError == null) {
            return GeneralErrorCode.BAD_REQUEST;
        }

        if (target instanceof ReviewReqDTO.CreateReviewDTO) {
            return resolveCreateReviewError(fieldError);
        }

        if (target instanceof ReviewReqDTO.GetMyReviewListDTO) {
            return resolveGetMyReviewListError(fieldError);
        }

        if (target instanceof MemberReqDTO.GetInfo) {
            return resolveMemberInfoError(fieldError);
        }

        if (target instanceof MemberReqDTO.JoinDTO) {
            return resolveMemberJoinError(fieldError);
        }

        if (target instanceof MissionReqDTO.GetProgressMissionListDTO) {
            return resolveMissionProgressError(fieldError);
        }

        return GeneralErrorCode.BAD_REQUEST;
    }

    private BaseErrorCode resolveCreateReviewError(FieldError fieldError) {
        return switch (fieldError.getField()) {
            case "memberId" -> "NotNull".equals(fieldError.getCode())
                    ? ReviewErrorCode.REVIEW_MEMBER_ID_REQUIRED
                    : ReviewErrorCode.REVIEW_MEMBER_ID_INVALID;
            case "score" -> ReviewErrorCode.REVIEW_SCORE_INVALID;
            case "body" -> ReviewErrorCode.REVIEW_BODY_REQUIRED;
            default -> GeneralErrorCode.BAD_REQUEST;
        };
    }

    private BaseErrorCode resolveGetMyReviewListError(FieldError fieldError) {
        return switch (fieldError.getField()) {
            case "memberId" -> "NotNull".equals(fieldError.getCode())
                    ? ReviewErrorCode.REVIEW_MEMBER_ID_REQUIRED
                    : ReviewErrorCode.REVIEW_MEMBER_ID_INVALID;
            case "size" -> ReviewErrorCode.REVIEW_SIZE_INVALID;
            case "sortType" -> ReviewErrorCode.REVIEW_SORT_TYPE_REQUIRED;
            case "cursorId", "cursorScore" -> ReviewErrorCode.REVIEW_CURSOR_INVALID;
            default -> GeneralErrorCode.BAD_REQUEST;
        };
    }

    private BaseErrorCode resolveMemberInfoError(FieldError fieldError) {
        if ("id".equals(fieldError.getField())) {
            if ("NotNull".equals(fieldError.getCode())) {
                return MemberErrorCode.MEMBER_ID_REQUIRED;
            }
            return MemberErrorCode.MEMBER_ID_INVALID;
        }

        return GeneralErrorCode.BAD_REQUEST;
    }

    private BaseErrorCode resolveMemberJoinError(FieldError fieldError) {
        return switch (fieldError.getField()) {
            case "name" -> MemberErrorCode.MEMBER_NAME_REQUIRED;
            case "password" -> MemberErrorCode.MEMBER_PASSWORD_REQUIRED;
            case "age" -> "NotNull".equals(fieldError.getCode())
                    ? MemberErrorCode.MEMBER_AGE_REQUIRED
                    : MemberErrorCode.MEMBER_AGE_INVALID;
            case "email" -> "NotBlank".equals(fieldError.getCode())
                    ? MemberErrorCode.MEMBER_EMAIL_REQUIRED
                    : MemberErrorCode.MEMBER_EMAIL_INVALID;
            case "gender" -> MemberErrorCode.MEMBER_GENDER_REQUIRED;
            case "nickName" -> MemberErrorCode.MEMBER_NICKNAME_REQUIRED;
            case "phoneNumber" -> MemberErrorCode.MEMBER_PHONE_NUMBER_REQUIRED;
            case "birthDate" -> MemberErrorCode.MEMBER_BIRTH_DATE_REQUIRED;
            default -> GeneralErrorCode.BAD_REQUEST;
        };
    }

    private BaseErrorCode resolveMissionProgressError(FieldError fieldError) {
        return switch (fieldError.getField()) {
            case "memberId" -> "NotNull".equals(fieldError.getCode())
                    ? MissionErrorCode.MISSION_MEMBER_ID_REQUIRED
                    : MissionErrorCode.MISSION_MEMBER_ID_INVALID;
            case "offset" -> "NotNull".equals(fieldError.getCode())
                    ? MissionErrorCode.MISSION_OFFSET_REQUIRED
                    : MissionErrorCode.MISSION_OFFSET_INVALID;
            case "limit" -> "NotNull".equals(fieldError.getCode())
                    ? MissionErrorCode.MISSION_LIMIT_REQUIRED
                    : MissionErrorCode.MISSION_LIMIT_INVALID;
            default -> GeneralErrorCode.BAD_REQUEST;
        };
    }
}
