package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Entities.User;
import com.coursitory.app.Entities.Video;
import com.coursitory.app.Services.CourseService;
import com.coursitory.app.Services.UserService;
import com.coursitory.app.Services.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Statement;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;

    @Autowired
    VideoService videoService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String id = userService.register(user);
        if (id == null) {
            return new ResponseEntity<>("Username already taken!", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Registration Successful!", HttpStatus.CREATED);
    }

    @PostMapping("/login/{admin}")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user, @PathVariable String admin) {
        String token = null;
        try {
            if (admin.equals("true")) {
                token = userService.verifyAdmin(user);
            } else {
                token = userService.verify(user);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Wrong username/password"), HttpStatus.UNAUTHORIZED);
        }
        if (token == null) {
            return new ResponseEntity<>(Map.of("error", "Wrong username/password"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
    }

//    @GetMapping("/testing")
//    public String testing() {
//        return "Hello !";
//    }

//    @PostMapping("/enroll/{courseId}")
//    public ResponseEntity<String> enrollInCourse(@PathVariable String courseId) {
//        try {
//            String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//            if (courseService.getCourseById(courseId) == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
//            }
//
//            User user = userService.findUserByUsername(username);
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            }
//
//            List<String> enrolledCourses = user.getEnrolled();
//            if (enrolledCourses == null) {
//                enrolledCourses = new ArrayList<>();
//            }
//            if (enrolledCourses.contains(courseId)) {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already enrolled in this course");
//            }
//
//            enrolledCourses.add(courseId);
//            user.setEnrolled(enrolledCourses);
//
//            // Save updated user
//            userService.save(user);
//
//            return ResponseEntity.ok("Successfully enrolled in course");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
//        }
//    }


    @GetMapping("/get/courses")
    public ResponseEntity<List<Course>> getEnrolledCourses(HttpServletRequest request) {
        try {
            // Extract username from the SecurityContext
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // Fetch user by username
            User user = userService.findUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Retrieve the list of enrolled courses
            List<String> enrolledCourseIds = user.getEnrolled();
            if (enrolledCourseIds == null || enrolledCourseIds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ArrayList<>());
            }

            // Fetch course details for the enrolled course IDs
            List<Course> enrolledCourses = courseService.getCoursesByIds(enrolledCourseIds);

            return ResponseEntity.ok(enrolledCourses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/check/enrollment/{courseId}")
    public boolean checkEnrollment(@PathVariable String courseId) {
        try {
            // Extract username from the SecurityContext
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // Fetch user by username
            User user = userService.findUserByUsername(username);
            if (user == null) {
                return false;
            }

            // Retrieve the list of enrolled courses
            List<String> enrolledCourseIds = user.getEnrolled();
            for (String enrolledCourseId : enrolledCourseIds) {
                if (Objects.equals(enrolledCourseId, courseId)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

}
