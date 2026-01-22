package com.careerx.entities;

import com.careerx.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "student_id"))
@ToString(callSuper = true)
public class StudentProfile extends BaseEntity {

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private Users user;

    // General Information
    @NotNull(message = "Date of Birth is required")
    private LocalDateTime dateOfBirth;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotBlank(message = "Mobile Number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile Number must be 10 digits")
    private String mobileNumber;

    private String preferredLanguage;

    // Education Details
    @NotNull(message = "Current Education Level is required")
    @Enumerated(EnumType.STRING)
    private EducationLevel currentEducationLevel;

    private String boardOrUniversity;

    private String schoolOrCollegeName;

    @Enumerated(EnumType.STRING)
    private Streams stream;

    private String currentYearOrSemester;

    private Double overallPercentageOrCGPA;

    // Gap Details
    private Boolean hasTakenGapYear = false;
    private Integer numberOfGapYears;

    @Enumerated(EnumType.STRING)
    private GapReason reasonForGap;

    // Career Interests
    @Enumerated(EnumType.STRING)
    private CareerInterest areasOfInterest;

    private String preferredCareerDomain;
    private String dreamJobOrRole;

    // Skills
    @Enumerated(EnumType.STRING)
    private TechnicalSkill technicalSkills;

    @Enumerated(EnumType.STRING)
    private SoftSkill softSkills;

    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;

    // Hobbies
    private String hobbies;
    private String extracurricularActivities;

    // Achievements
    private String academicAchievements;
    private String scholarships;
    private String rankOrMeritCertificates;
    private String competitionsOrHackathons;
    private String sportsOrCulturalAchievements;
    private String certifications;
    private String certificationCourseName;
    private String certificationPlatform;
    private Integer certificationYear;

    // Entrance Exams
    private Boolean appearedForCompetitiveExams = false;
    private String examName;
    private String examScoreOrRank;
    private Integer examYear;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    public Long getStudentId() {
        return getId();
    }
}