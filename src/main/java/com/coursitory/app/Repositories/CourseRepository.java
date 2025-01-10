package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.Course;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, ObjectId> {
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Course> findByTitleContaining(String pattern);

    @Query("{'description': {$regex: ?0, $options: 'i'}}")
    List<Course> findByDescriptionContaining(String pattern);

    @Query("{'tags': {$regex: ?0, $options: 'i'}}")
    List<Course> findByTagsContaining(String pattern);
}
