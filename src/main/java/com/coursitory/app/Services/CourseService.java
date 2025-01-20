package com.coursitory.app.Services;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Entities.Image;
import com.coursitory.app.Repositories.CourseRepository;
import com.coursitory.app.Repositories.ImageRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class CourseService {
    @Autowired
    VideoService videoService;

    @Autowired
    PDFService pdfService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ImageRepository imageRepository;

    public void createCourse(Course course, MultipartFile thumbnail) throws IOException {
        if (course.getDescription() == null) {
            course.setDescription("");
        }
        if (course.getTags() == null) {
            course.setTags(new ArrayList<>());
        }

        // Set default values for the fields that the client does not provide
        course.setRating(0); // Default rating is 0
        course.setVideoList(new ArrayList<>()); // Default empty list for videoList
        course.setPdfList(new ArrayList<>()); // Default empty list for pdfList
        if (thumbnail != null) {
            Image img = new Image(thumbnail.getContentType(), thumbnail.getBytes());
            imageRepository.save(img);
            course.setThumbnail(img.getId());
        }
        // Save the course in the database
        courseRepository.save(course);
    }

    public void saveUpdatedCourse(Course course) {
        courseRepository.save(course);
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String courseId) {
        ObjectId id = new ObjectId(courseId);
        return courseRepository.findById(id).orElse(null);
    }

    public List<Course> getCoursesByIds(List<String> enrolledCourseIds) {
        List<Course> list = new ArrayList<>();
        for (int i = 0; i < enrolledCourseIds.size(); i++) {
            ObjectId id = new ObjectId(enrolledCourseIds.get(i));
            Optional<Course> course = courseRepository.findById(id);
            course.ifPresent(list::add);
        }
        return list;
    }

    public Map<String, List<Course>> searchCourses(String pattern) {
        List<Course> byTitle = courseRepository.findByTitleContaining(pattern);
        List<Course> byDesc = courseRepository.findByDescriptionContaining(pattern);
        List<Course> byTags = courseRepository.findByTagsContaining(pattern);

        List<Course> result = new ArrayList<>();
        Set<ObjectId> addedCourseIds = new HashSet<>(); // Use a Set to track unique Course IDs (assuming Course has a unique ID field)

        for (Course course : byTitle) {
            if (addedCourseIds.add(course.getId())) { // Add to Set and check if it's unique
                result.add(course);
            }
        }

        for (Course course : byDesc) {
            if (addedCourseIds.add(course.getId())) { // Add to Set and check if it's unique
                result.add(course);
            }
        }

        for (Course course : byTags) {
            if (addedCourseIds.add(course.getId())) { // Add to Set and check if it's unique
                result.add(course);
            }
        }

        Map<String, List<Course>> response = new HashMap<>();
        response.put("data", result);
        return response;
    }

    public boolean deleteCourse(String courseId) {
        Optional<Course> course = courseRepository.findById(new ObjectId(courseId));
        if (!course.isPresent()) {
            return false;
        }
        List<String> vidL = course.get().getVideoList();
        for (String id : vidL) {
            videoService.deleteVideo(id);
        }

        List<String> pdfL = course.get().getPdfList();
        for (String id : pdfL) {
            pdfService.deletePDF(id);
        }
        if (course.get().getThumbnail() != null) {
            imageRepository.deleteById(course.get().getThumbnail());
        }
        courseRepository.delete(course.get());
        return true;
    }
}
