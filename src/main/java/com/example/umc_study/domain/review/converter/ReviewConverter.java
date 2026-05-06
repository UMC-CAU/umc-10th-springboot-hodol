package com.example.umc_study.domain.review.converter;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.mission.entity.Store;
import com.example.umc_study.domain.review.dto.ReviewReqDTO;
import com.example.umc_study.domain.review.dto.ReviewResDTO;
import com.example.umc_study.domain.review.entity.Reply;
import com.example.umc_study.domain.review.entity.Review;
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
