package com.coursitory.app.Services;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Repositories.CourseRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;

    public void createCourse(Course course){
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

        // Save the course in the database
        courseRepository.save(course);
    }
    public void saveUpdatedCourse(Course course){
        courseRepository.save(course);
    }

    public List<Course> getAll(){
        return courseRepository.findAll();
    }
    public Course getCourseById(String courseId){
        ObjectId id = new ObjectId(courseId);
        return  courseRepository.findById(id).orElse(null);
    }
}
