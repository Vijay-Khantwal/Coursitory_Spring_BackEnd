package com.coursitory.app.Repositories;

import com.coursitory.app.Entities.PaymentLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentLogRepository extends MongoRepository<PaymentLog, String> {
    boolean existsByPaymentId(String paymentId);
}
