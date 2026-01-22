package com.careerx.apiresponses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudentProfileResponse {
    private Long studentId;
    private Long userId;

    private GeneralInformation generalInformation;
    private EducationDetails educationDetails;
    private CareerInterests careerInterests;
    private SkillsAndStrengths skillsAndStrengths;
    private HobbiesAndInterests hobbiesAndInterests;
    private AchievementsAndCertifications achievementsAndCertifications;
    private EntranceExams entranceExams;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
