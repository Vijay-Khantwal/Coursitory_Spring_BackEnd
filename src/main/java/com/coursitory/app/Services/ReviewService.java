package com.coursitory.app.Services;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Entities.Review;
import com.coursitory.app.Repositories.CourseRepository;
import com.coursitory.app.Repositories.ReviewRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class ReviewService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public Review addOrUpdateReview(String courseId, Review review, String username) {
        Course course = courseRepository.findById(new ObjectId(courseId))
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Review existingReview = reviewRepository.findByCourseIdAndUsername(courseId, username);

        if (existingReview != null) {
            int existingRating = existingReview.getRating();
            course.setRating(
                    (course.getRating() * course.getReviewCount() - existingRating) / (course.getReviewCount() - 1)
            );
            course.setReviewCount(course.getReviewCount() - 1);
            course.getReviewIds().remove(existingReview.getReviewId());
            reviewRepository.delete(existingReview);
        }

        review.setCourseId(courseId);
        review.setUsername(username);
        review.setDate(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);

        double newAvgRating = (course.getRating() * course.getReviewCount() + review.getRating())
                / (course.getReviewCount() + 1);
        course.setRating(newAvgRating);
        course.setReviewCount(course.getReviewCount() + 1);
        if (course.getReviewIds() == null) {
            course.setReviewIds(new ArrayList<>());
        }
        course.getReviewIds().add(savedReview.getReviewId());
        courseRepository.save(course);

        return savedReview;
    }

    public Page<Review> getReviewsByCourseId(String courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return reviewRepository.findByCourseId(courseId, pageable);
    }

    public Review hasUserReviewedCourse(String courseId, String username) {
        return reviewRepository.findByCourseIdAndUsername(courseId, username);
    }
}
