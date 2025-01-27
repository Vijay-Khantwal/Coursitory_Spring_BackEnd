package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Entities.User;
import com.coursitory.app.Services.CourseService;
import com.coursitory.app.Services.EnrollmentService;
import com.coursitory.app.Services.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${rzp_key_id}")
    private String keyId;

    @Value("${rzp_key_secret}")
    private String secret;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    UserService userService;

    @GetMapping("/test/exist/{userId}/{courseId}")
    public boolean demo(@PathVariable String userId, @PathVariable String courseId){
        return enrollmentService.isUserAlreadyEnrolled(userId,courseId);
    }

    @GetMapping("/createOrder/{courseId}")
    public ResponseEntity<Map<String, String>> createOrder(@PathVariable String courseId) {
        Map<String, String> responseMap = new HashMap<>();

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findUserByUsername(username);
            if(user == null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (enrollmentService.isUserAlreadyEnrolled(user.getId().toString(), courseId)) {
                System.out.println("User already enrolled!");
                responseMap.put("error","User already enrolled in this course");
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }

            // Fetch course details from the database
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                responseMap.put("error", "Course not found");
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }

            int amountInPaise =  course.getPrice() * 100; // Convert price to paise
            if(amountInPaise <= 0){
                enrollmentService.enrollUser(user.getId().toString(),courseId,"FREE","FREE","FREE","SUCCESS");
                responseMap.put("free","true");
                return new ResponseEntity<>(responseMap,HttpStatus.OK);
            }

            RazorpayClient razorpayClient = new RazorpayClient(keyId, secret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", enrollmentService.generateReceipt(user.getId().toString(), courseId));

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");

            // Save the order details in MongoDB
            enrollmentService.savePaymentLog(user.getId().toString(), courseId, orderId, "", "", "PENDING");

            responseMap.put("order_id", orderId);
            responseMap.put("amount", String.valueOf(amountInPaise));
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        } catch (RazorpayException e){
            responseMap.put("error", "Razorpay exception occurred: " + e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, String> paymentData) {
        String razorpayPaymentId = paymentData.get("razorpay_payment_id");
        String razorpayOrderId = paymentData.get("razorpay_order_id");
        String razorpaySignature = paymentData.get("razorpay_signature");
        String courseId = paymentData.get("courseId");

        try {
            if (!verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature)) {
                return new ResponseEntity<>("Invalid payment signature", HttpStatus.BAD_REQUEST);
            }

            if (enrollmentService.isPaymentProcessed(razorpayPaymentId)) {
                return new ResponseEntity<>("Payment has already been processed", HttpStatus.BAD_REQUEST);
            }
            enrollmentService.enrollUser("",courseId, razorpayPaymentId, razorpayOrderId, razorpaySignature, "SUCCESS");

            return new ResponseEntity<>("User successfully enrolled", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            enrollmentService.enrollUser("",courseId, razorpayPaymentId, razorpayOrderId, razorpaySignature, "FAILED");
            return new ResponseEntity<>("Payment verification failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // SHA-256 Signature Verification
    private boolean verifySignature(String orderId, String paymentId, String signature) throws Exception {
        String payload = orderId + "|" + paymentId;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        byte[] hash = mac.doFinal(payload.getBytes());
        StringBuilder generatedSignature = new StringBuilder();
        for (byte b : hash) {
            generatedSignature.append(String.format("%02x", b));
        }
        return generatedSignature.toString().equals(signature);
    }


}
