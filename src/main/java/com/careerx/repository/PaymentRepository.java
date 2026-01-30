package com.careerx.repository;

import com.careerx.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudentIdOrderByCreatedDateDesc(Long studentId);

    Optional<Payment> findByRazorpayOrderId(String orderId);

    Optional<Payment> findByIdAndStudentIdAndPaymentStatus(Long id, Long studentId, String status);

    Optional<Payment> findTopByStudentIdAndPaymentTypeAndPaymentStatusOrderByCompletedAtDesc(Long studentId,
            String type, String status);
}
