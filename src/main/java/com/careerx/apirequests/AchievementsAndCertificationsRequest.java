package com.careerx.apirequests;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementsAndCertificationsRequest {
    private String academicAchievements;
    private String scholarships;
    private String rankOrMeritCertificates;
    private String competitionsOrHackathons;
    private String sportsOrCulturalAchievements;
    private String certifications;
    private String certificationCourseName;
    private String certificationPlatform;
    private Integer certificationYear;
}
