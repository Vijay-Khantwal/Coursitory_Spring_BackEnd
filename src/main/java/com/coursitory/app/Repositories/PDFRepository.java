package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.PDF;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PDFRepository extends MongoRepository<PDF, ObjectId> {
}
