package com.careerx.repository;

import com.careerx.entities.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    java.util.List<StudentProfile> findByUserId(Long userId);
}