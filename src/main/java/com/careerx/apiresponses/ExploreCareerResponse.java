package com.careerx.apiresponses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExploreCareerResponse {

    private Long careerId;
    private String title;
    private String description;
    private String imageUrl;
    private String requiredEducation;
    private String skillsRequired;
    private String jobSector;
    private BigDecimal averageSalary;
    private String careerPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}