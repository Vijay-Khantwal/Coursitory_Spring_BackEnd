package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Repositories.CourseRepository;
import com.coursitory.app.Services.CourseService;
import com.coursitory.app.Services.PDFService;
import com.coursitory.app.Services.VideoService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class CourseController {
    @Autowired
    CourseService courseService;
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    VideoService videoService;

    @Autowired
    PDFService pdfService;

    // Just a test api for checking active connection from frontend
    @GetMapping("/ping")
    public String testing(){
        return "Sucess";
    }


//    @PostMapping("/create/course")
//    public ResponseEntity<Course> createCourse(@RequestParam String title,
//                                               @RequestParam String description, @RequestParam List<String> tags,
//                                               @RequestParam(value = "file", required = false) MultipartFile thumbnail) {
//
//        try {
//            // Ensure that title is provided (mandatory field)
//            if (title.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//            Course course = new Course(title, description, tags);
//            courseService.createCourse(course, thumbnail);
//            return new ResponseEntity<>(course, HttpStatus.CREATED);
//
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @GetMapping("/get/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        try {
            List<Course> courses = courseService.getAll();
            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.ok().body(courses);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/search/course/{pattern}")
    public ResponseEntity<Map<String, List<Course>>> searchCourses(@PathVariable String pattern) {
        Map<String, List<Course>> result = courseService.searchCourses(pattern);
        return ResponseEntity.ok(result);
    }

//    @PostMapping("/upload_video/{courseId}")
//    public ResponseEntity<String> uploadVideo(@PathVariable String courseId, @RequestParam("file") MultipartFile file, @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail, @RequestParam("title") String title, @RequestParam(value = "description", required = false, defaultValue = "No description yet!") String description) throws IOException {
//
//        Course course = courseService.getCourseById(courseId);
//        if (course == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        String videoId = videoService.uploadVideo(file, thumbnail, title, description);
//
//        course.getVideoList().add(videoId);
//        courseRepository.save(course);
//
//        return new ResponseEntity<>(videoId, HttpStatus.OK);
//    }
//
//    @PostMapping("/upload_pdf/{courseId}")
//    public ResponseEntity<String> uploadPdf(@PathVariable String courseId, @RequestParam("file") MultipartFile file, @RequestParam("title") String title) throws IOException {
//
//        if (title.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        Course course = courseService.getCourseById(courseId);
//        if (course == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        String pdfId = pdfService.uploadPDF(file, title);
//
//        course.getPdfList().add(pdfId);
//        courseRepository.save(course);
//        return new ResponseEntity<>(pdfId, HttpStatus.OK);
//    }

}
