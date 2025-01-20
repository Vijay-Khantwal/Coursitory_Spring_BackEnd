package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review,String> {
    Page<Review> findByCourseId(String courseId, Pageable pageable);
    Review findByCourseIdAndUsername(String courseId, String username);
}
