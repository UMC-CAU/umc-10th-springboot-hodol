package com.example.umc_study.domain.review.converter;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.mission.entity.Store;
import com.example.umc_study.domain.review.dto.ReviewReqDTO;
import com.example.umc_study.domain.review.dto.ReviewResDTO;
import com.example.umc_study.domain.review.entity.Reply;
import com.example.umc_study.domain.review.entity.Review;
import com.example.umc_study.domain.review.enums.ReviewSortType;
import org.springframework.data.domain.Slice;
import org.springframework.util.StringUtils;

public class ReviewConverter {

    private static final int TITLE_MAX_LENGTH = 50;

    public static Review toReview(ReviewReqDTO.CreateReviewDTO request, Member member, Store store, Reply reply) {
        Review review = Review.builder()
                .title(resolveTitle(request.getTitle(), request.getBody()))
                .score(request.getScore())
                .body(request.getBody())
                .member(member)
                .store(store)
                .build();

        review.assignReply(reply);
        return review;
    }

    public static ReviewResDTO.CreateResultDTO toCreateResultDTO(Review review) {
        return ReviewResDTO.CreateResultDTO.builder()
                .reviewId(review.getId())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static ReviewResDTO.MyReviewListDTO toMyReviewListDTO(Slice<Review> reviewSlice, ReviewSortType sortType) {
        java.util.List<ReviewResDTO.MyReviewDetailDTO> reviewList = reviewSlice.getContent().stream()
                .map(ReviewConverter::toMyReviewDetailDTO)
                .toList();

        Review lastReview = reviewSlice.hasNext() && !reviewSlice.isEmpty()
                ? reviewSlice.getContent().get(reviewSlice.getNumberOfElements() - 1)
                : null;

        return ReviewResDTO.MyReviewListDTO.builder()
                .reviewList(reviewList)
                .pagination(
                        ReviewResDTO.CursorPaginationDTO.builder()
                                .nextCursorId(lastReview != null ? lastReview.getId() : null)
                                .nextCursorScore(lastReview != null && sortType == ReviewSortType.SCORE ? lastReview.getScore() : null)
                                .size(reviewSlice.getSize())
                                .hasNext(reviewSlice.hasNext())
                                .build()
                )
                .build();
    }

    private static ReviewResDTO.MyReviewDetailDTO toMyReviewDetailDTO(Review review) {
        return ReviewResDTO.MyReviewDetailDTO.builder()
                .reviewId(review.getId())
                .storeName(review.getStore().getName())
                .title(review.getTitle())
                .body(review.getBody())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private static String resolveTitle(String title, String body) {
        if (StringUtils.hasText(title)) {
            return truncate(title.trim());
        }

        return truncate(body.trim().replaceAll("\\s+", " "));
    }

    private static String truncate(String value) {
        return value.length() <= TITLE_MAX_LENGTH ? value : value.substring(0, TITLE_MAX_LENGTH);
    }
}
