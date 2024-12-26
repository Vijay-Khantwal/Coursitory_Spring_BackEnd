package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.Course;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, ObjectId> {
}
