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

    /**
     * Adds or updates a review for a course by a specific user.
     */
    public Review addOrUpdateReview(String courseId, Review review, String username) {
        // Fetch the course
        Course course = courseRepository.findById(new ObjectId(courseId))
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if the user has already reviewed this course
        Review existingReview = reviewRepository.findByCourseIdAndUsername(courseId, username);

        if (existingReview != null) {
            // Remove the existing review from the course
            int existingRating = existingReview.getRating();
            course.setRating(
                    (course.getRating() * course.getReviewCount() - existingRating) / (course.getReviewCount() - 1)
            );
            course.setReviewCount(course.getReviewCount() - 1);
            course.getReviewIds().remove(existingReview.getReviewId());
            reviewRepository.delete(existingReview);
        }

        // Save the new review
        review.setCourseId(courseId);
        review.setUsername(username);
        review.setDate(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);

        // Update course average rating and review count
        double newAvgRating = (course.getRating() * course.getReviewCount() + review.getRating())
                / (course.getReviewCount() + 1);
        course.setRating(newAvgRating);
        course.setReviewCount(course.getReviewCount() + 1);

        // Add the new review ID to the course
        if (course.getReviewIds() == null) {
            course.setReviewIds(new ArrayList<>());
        }
        course.getReviewIds().add(savedReview.getReviewId());
        courseRepository.save(course);

        return savedReview;
    }

    public Page<Review> getReviewsByCourseId(String courseId, int page, int size) {
        // Create a Pageable object with sorting by date in descending order
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return reviewRepository.findByCourseId(courseId, pageable);
    }

    public Review hasUserReviewedCourse(String courseId, String username) {
        return reviewRepository.findByCourseIdAndUsername(courseId, username);
    }
}
