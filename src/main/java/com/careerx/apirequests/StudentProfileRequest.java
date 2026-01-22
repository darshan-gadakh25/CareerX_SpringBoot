package com.careerx.apirequests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileRequest {

    @NotNull(message = "General Information is required")
    @Valid
    private GeneralInformationRequest generalInformation;

    @NotNull(message = "Education Details is required")
    @Valid
    private EducationDetailsRequest educationDetails;

    @Valid
    private CareerInterestsRequest careerInterests;

    @Valid
    private SkillsAndStrengthsRequest skillsAndStrengths;

    @Valid
    private HobbiesAndInterestsRequest hobbiesAndInterests;

    @Valid
    private AchievementsAndCertificationsRequest achievementsAndCertifications;

    @Valid
    private EntranceExamsRequest entranceExams;
}
