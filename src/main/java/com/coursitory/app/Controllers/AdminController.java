package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Entities.User;
import com.coursitory.app.Services.AdminService;
import com.coursitory.app.Services.CourseService;
import com.coursitory.app.Services.PDFService;
import com.coursitory.app.Services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    PDFService pdfService;

    @Autowired
    AdminService adminService;

    @Autowired
    CourseService courseService;

    @Autowired
    VideoService videoService;

    @PostMapping("/login")
    public String adminLogin(@RequestBody User user) {
        return adminService.verifyAdmin(user);
    }

    //###################################### PDF ######################################################

    @PostMapping("/upload/pdf")
    public ResponseEntity<String> uploadPDF(@RequestParam("file") MultipartFile file){
        try {
            String pdfId = pdfService.uploadPDF(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("fieldId :- "+pdfId);
        }
        catch (IOException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in uploading! " + e.getMessage());
        }
    }

    @DeleteMapping("delete/pdf/{id}")
    public  ResponseEntity<String> deletePDF(@PathVariable("id") String pdfId){
        if(pdfService.deletePDF(pdfId)){
            return ResponseEntity.ok().body("PDF successfully deleted");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PdfId invalid! ");
        }
    }

    //###################################### ************** ######################################################

    //###################################### CourseContent ######################################################

    @PostMapping("/create/Course")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        try {
            // Ensure that title is provided (mandatory field)
            if (course.getTitle() == null || course.getTitle().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            courseService.createCourse(course);
            return new ResponseEntity<>(course, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload_video/{courseId}")
    public ResponseEntity<String> uploadVideo(@PathVariable String courseId, @RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                                              @RequestParam("title") String title,
                                              @RequestParam(value = "description", required = false, defaultValue = "No description yet!") String description) throws IOException {

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String videoId = videoService.uploadVideo(file, thumbnail, title, description);

        course.getVideoList().add(videoId);
        courseService.saveUpdatedCourse(course);

        return new ResponseEntity<>(videoId, HttpStatus.OK);
    }

    @PostMapping("/upload_pdf/{courseId}")
    public ResponseEntity<String> uploadPdf(@PathVariable String courseId, @RequestParam("file") MultipartFile file) throws IOException {

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String pdfId = pdfService.uploadPDF(file);

        course.getPdfList().add(pdfId);
        courseService.saveUpdatedCourse(course);

        return new ResponseEntity<>(pdfId, HttpStatus.OK);
    }

    //###################################### ************* ######################################################


}

