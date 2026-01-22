package com.careerx.repository;

import com.careerx.entities.ExploreCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExploreCareerRepository extends JpaRepository<ExploreCareer, Long> {
    
    List<ExploreCareer> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT e FROM ExploreCareer e WHERE e.jobSector = :sector ORDER BY e.createdAt DESC")
    List<ExploreCareer> findByJobSector(@Param("sector") String sector);
    
    @Query("SELECT e FROM ExploreCareer e WHERE e.title LIKE %:keyword% OR e.description LIKE %:keyword%")
    List<ExploreCareer> searchByKeyword(@Param("keyword") String keyword);
}