package com.example.umc_study.domain.review.service;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.domain.mission.entity.Store;
import com.example.umc_study.domain.mission.repository.StoreRepository;
import com.example.umc_study.domain.review.converter.ReviewConverter;
import com.example.umc_study.domain.review.dto.ReviewReqDTO;
import com.example.umc_study.domain.review.dto.ReviewResDTO;
import com.example.umc_study.domain.review.entity.Reply;
import com.example.umc_study.domain.review.entity.Review;
import com.example.umc_study.domain.review.exception.ReviewException;
import com.example.umc_study.domain.review.exception.code.ReviewErrorCode;
import com.example.umc_study.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ReviewResDTO.CreateResultDTO createReview(Long storeId, ReviewReqDTO.CreateReviewDTO request) {
        validateRequest(request);

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_MEMBER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_STORE_NOT_FOUND));

        Reply emptyReply = Reply.builder()
                .body("")
                .build();

        Review newReview = ReviewConverter.toReview(request, member, store, emptyReply);
        Review savedReview = reviewRepository.save(newReview);
        return ReviewConverter.toCreateResultDTO(savedReview);
    }

    private void validateRequest(ReviewReqDTO.CreateReviewDTO request) {
        if (request.getMemberId() == null) {
            throw new ReviewException(ReviewErrorCode.REVIEW_MEMBER_ID_REQUIRED);
        }

        if (request.getScore() == null || request.getScore() < 0.0f || request.getScore() > 5.0f) {
            throw new ReviewException(ReviewErrorCode.REVIEW_SCORE_INVALID);
        }

        if (!StringUtils.hasText(request.getBody())) {
            throw new ReviewException(ReviewErrorCode.REVIEW_BODY_REQUIRED);
        }
    }
}
