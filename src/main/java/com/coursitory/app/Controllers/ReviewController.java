package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.Review;
import com.coursitory.app.Entities.User;
import com.coursitory.app.Services.ReviewService;
import com.coursitory.app.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    UserService userService;

    @Autowired
    PagedResourcesAssembler<Review> pagedResourcesAssembler;

    @PostMapping("/add/{courseId}")
    public ResponseEntity<Review> addReview(@PathVariable String courseId, @RequestBody Review review) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch user by username
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Review savedReview = reviewService.addOrUpdateReview(courseId, review, username);
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/get/{courseId}")
    public PagedModel<?> getReviews(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") int page,  // Default page = 0
            @RequestParam(defaultValue = "5") int size  // Default size = 5
    ) {
        // Fetch paginated reviews
        if (courseId == null || courseId.isEmpty()) {
            throw new IllegalArgumentException("Course ID must not be null or empty");
        }
        Page<Review> reviews = reviewService.getReviewsByCourseId(courseId, page, size);
        // Convert to PagedModel for proper serialization
        return pagedResourcesAssembler.toModel(reviews);
    }

    @GetMapping("/{courseId}/has-reviewed")
    public ResponseEntity<Review> hasUserReviewed(
            @PathVariable String courseId
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch user by username
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Review review = reviewService.hasUserReviewedCourse(courseId, username);
        if(review  != null){
            return new ResponseEntity<>(review,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}


