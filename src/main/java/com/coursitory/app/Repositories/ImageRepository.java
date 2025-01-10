package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.Image;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<Image, ObjectId> {
}
