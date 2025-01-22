package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.Enrollment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {
    // Check if the user is already enrolled in a course
    Enrollment findByUserIdAndCourseId(String userId, String courseId);

    void deleteByUserIdAndCourseId(String userId,String courseId);

    // Check if the payment has already been processed
    boolean existsByPaymentId(String paymentId);

    Enrollment findByOrderId(String orderId);
}
