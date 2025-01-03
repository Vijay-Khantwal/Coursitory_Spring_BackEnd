package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.PDF;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PDFRepository extends MongoRepository<PDF, ObjectId> {
}
