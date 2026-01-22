package com.careerx.apirequests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExploreCareerRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String imageUrl;

    private String requiredEducation;

    private String skillsRequired;

    private String jobSector;

    private BigDecimal averageSalary;

    private String careerPath;
}