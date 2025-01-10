package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Entities.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends MongoRepository<Video,String> {
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Video> findVidsByTitle(String pattern);
}
