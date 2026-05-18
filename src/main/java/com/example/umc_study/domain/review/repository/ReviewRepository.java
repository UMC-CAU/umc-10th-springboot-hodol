package com.example.umc_study.domain.review.repository;

import com.example.umc_study.domain.review.entity.Review;
import com.example.umc_study.domain.review.repository.projection.MyReviewSummaryProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    long countByMember_Id(Long memberId);

    @EntityGraph(attributePaths = {"store"})
    @Query("""
            select r
            from Review r
            where r.member.id = :memberId
              and (:cursorId is null or r.id < :cursorId)
            order by r.id desc
            """)
    Slice<Review> findMyReviewsByIdCursor(
            @Param("memberId") Long memberId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
            select
                r.id as reviewId,
                s.name as storeName,
                r.title as title,
                r.body as body,
                r.score as score,
                r.createdAt as createdAt
            from Review r
            join r.store s
            where r.member.id = :memberId
              and (:cursorId is null or r.id < :cursorId)
            order by r.id desc
            """)
    Slice<MyReviewSummaryProjection> findMyReviewSummariesByIdCursor(
            @Param("memberId") Long memberId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"store"})
    @Query("""
            select r
            from Review r
            where r.member.id = :memberId
              and (
                    :cursorScore is null
                    or r.score < :cursorScore
                    or (r.score = :cursorScore and r.id < :cursorId)
              )
            order by r.score desc, r.id desc
            """)
    Slice<Review> findMyReviewsByScoreCursor(
            @Param("memberId") Long memberId,
            @Param("cursorScore") Float cursorScore,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
            select
                r.id as reviewId,
                s.name as storeName,
                r.title as title,
                r.body as body,
                r.score as score,
                r.createdAt as createdAt
            from Review r
            join r.store s
            where r.member.id = :memberId
              and (
                    :cursorScore is null
                    or r.score < :cursorScore
                    or (r.score = :cursorScore and r.id < :cursorId)
              )
            order by r.score desc, r.id desc
            """)
    Slice<MyReviewSummaryProjection> findMyReviewSummariesByScoreCursor(
            @Param("memberId") Long memberId,
            @Param("cursorScore") Float cursorScore,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
