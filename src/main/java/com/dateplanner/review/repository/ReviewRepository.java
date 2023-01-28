package com.dateplanner.review.repository;

import com.dateplanner.review.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"user", "place"})
    List<Review> findByKpid(String placeId);

    @EntityGraph(attributePaths = {"user", "place"})
    List<Review> findByUser_Uid(String placeId);

    @Override
    @EntityGraph(attributePaths = {"user", "place"})
    Optional<Review> findById(Long id);
}