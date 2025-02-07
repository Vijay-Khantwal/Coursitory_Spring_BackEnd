package com.coursitory.app.Services;

import com.coursitory.app.Entities.Enrollment;
import com.coursitory.app.Entities.User;
import com.coursitory.app.Repositories.EnrollmentRepository;
import com.coursitory.app.Repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    UserRepository userRepository;

    // Enroll user and save payment details together (for successful payment)
    @Transactional
    public void enrollUser(String userId,String courseId, String paymentId, String orderId, String signature, String status) {
        // Check if the orderId already exists
        if(orderId.equals("FREE")){
            savePaymentLog(userId,courseId,orderId,paymentId,signature,status);
            Optional<User> user = userRepository.findById(new ObjectId(userId));
            if (user.isPresent()) {
                user.get().getEnrolled().add(courseId);
                userRepository.save(user.get());
            }
            return;
        }

        Enrollment existingEnrollment = enrollmentRepository.findByOrderId(orderId);

        if (existingEnrollment != null) {
            String receipt = generateReceipt(existingEnrollment.getUserId(), courseId);
            existingEnrollment.setPaymentId(paymentId);
            existingEnrollment.setSignature(signature);
            existingEnrollment.setStatus(status);
            existingEnrollment.setReceipt(receipt);
            existingEnrollment.setEnrolledOn(String.valueOf(System.currentTimeMillis()));
            enrollmentRepository.save(existingEnrollment);

            if (existingEnrollment.getStatus().equals("SUCCESS")) {
                Optional<User> user = userRepository.findById(new ObjectId(existingEnrollment.getUserId()));
                if (user.isPresent()) {
                    user.get().getEnrolled().add(courseId);
                    userRepository.save(user.get());
                }
            }

        }

    }

    public boolean isUserAlreadyEnrolled(String userId, String courseId) {
        Optional<User> user = userRepository.findById(new ObjectId(userId));

        if (user.isPresent() && user.get().getEnrolled().contains(courseId)) {
            return true;
        } else {
            enrollmentRepository.deleteByUserIdAndCourseId(userId, courseId);
        }
        return false;
    }

    public boolean isPaymentProcessed(String paymentId) {
        return enrollmentRepository.existsByPaymentId(paymentId);
    }

    public void savePaymentLog(String userId, String courseId, String orderId, String paymentId, String signature, String status) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setOrderId(orderId);
        enrollment.setPaymentId(paymentId);
        enrollment.setSignature(signature);
        enrollment.setStatus(status);
        enrollmentRepository.save(enrollment);
    }

    public String generateReceipt(String userId, String courseId) {
        try {
            String input = userId + courseId + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                hexString.append(String.format("%02x", hash[i]));
            }
            return "rec_" + hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "rec_" + System.currentTimeMillis();
        }
    }
}
