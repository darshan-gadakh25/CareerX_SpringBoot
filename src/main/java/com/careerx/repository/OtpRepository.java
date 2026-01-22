package com.careerx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.careerx.entities.OTP;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OTP, Long> {

    List<OTP> findByEmailAndIsUsedFalse(String email);

    Optional<OTP> findFirstByEmailAndOtpCodeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String email, String otpCode, LocalDateTime now
    );
}