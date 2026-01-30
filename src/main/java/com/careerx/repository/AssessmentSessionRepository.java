package com.careerx.repository;

import com.careerx.entities.AssessmentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentSessionRepository extends JpaRepository<AssessmentSession, Long> {

    List<AssessmentSession> findByUserId(Long userId);

    List<AssessmentSession> findByUserIdAndIsCompletedFalse(Long userId);

    Optional<AssessmentSession> findByPaymentId(Long paymentId);

    List<AssessmentSession> findByUserIdOrderByCreatedAtDesc(Long userId);
}
