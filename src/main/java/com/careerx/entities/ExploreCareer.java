package com.careerx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "explore_careers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "career_id"))
@ToString(callSuper = true)
public class ExploreCareer extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private String requiredEducation;

    private String skillsRequired;

    private String jobSector;

    @Column(precision = 10, scale = 2)
    private BigDecimal averageSalary;

    private String careerPath;

    public Long getCareerId() {
        return getId();
    }
}