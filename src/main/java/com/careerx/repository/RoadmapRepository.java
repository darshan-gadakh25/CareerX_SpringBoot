package com.careerx.repository;

import com.careerx.entities.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByStudentIdOrderByCreatedDateDesc(Long studentId);

    Optional<Roadmap> findByPaymentId(Long paymentId);
}
