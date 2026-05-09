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
import com.example.umc_study.domain.review.enums.ReviewSortType;
import com.example.umc_study.domain.review.exception.ReviewException;
import com.example.umc_study.domain.review.exception.code.ReviewErrorCode;
import com.example.umc_study.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    public ReviewResDTO.MyReviewListDTO getMyReviews(ReviewReqDTO.GetMyReviewListDTO request) {
        memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_MEMBER_NOT_FOUND));

        validateCursor(request);

        Pageable pageable = PageRequest.of(0, request.getSize());
        Slice<Review> reviewSlice = switch (request.getSortType()) {
            case SCORE -> reviewRepository.findMyReviewsByScoreCursor(
                    request.getMemberId(),
                    request.getCursorScore(),
                    request.getCursorId(),
                    pageable
            );
            case ID -> reviewRepository.findMyReviewsByIdCursor(
                    request.getMemberId(),
                    request.getCursorId(),
                    pageable
            );
        };

        return ReviewConverter.toMyReviewListDTO(reviewSlice, request.getSortType());
    }

    @Transactional
    public ReviewResDTO.CreateResultDTO createReview(Long storeId, ReviewReqDTO.CreateReviewDTO request) {
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

    private void validateCursor(ReviewReqDTO.GetMyReviewListDTO request) {
        if (request.getSortType() == ReviewSortType.SCORE) {
            boolean bothNull = request.getCursorId() == null && request.getCursorScore() == null;
            boolean bothPresent = request.getCursorId() != null && request.getCursorScore() != null;
            if (!bothNull && !bothPresent) {
                throw new ReviewException(ReviewErrorCode.REVIEW_CURSOR_INVALID);
            }
            return;
        }

        if (request.getCursorScore() != null) {
            throw new ReviewException(ReviewErrorCode.REVIEW_CURSOR_INVALID);
        }
    }
}
