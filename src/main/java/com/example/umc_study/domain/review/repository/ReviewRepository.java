package com.example.umc_study.domain.review.repository;

import com.example.umc_study.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
