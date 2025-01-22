package com.coursitory.app.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "enrollments")
public class Enrollment {

    @Id
    private String id;           // MongoDB's auto-generated _id
    private String userId;       // User who is enrolling
    private String courseId;     // Course that the user is enrolling in
    private String paymentId;    // Razorpay payment ID
    private String orderId;      // Razorpay order ID
    private String signature;    // Razorpay signature
    private String status;       // Payment status: 'SUCCESS' or 'FAILED'
    private String enrolledOn;   // Timestamp (ISODate) when enrolled
    private String receipt;      // Unique receipt for the payment

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnrolledOn() {
        return enrolledOn;
    }

    public void setEnrolledOn(String enrolledOn) {
        this.enrolledOn = enrolledOn;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
