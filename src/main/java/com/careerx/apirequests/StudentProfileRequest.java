package com.careerx.apirequests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileRequest {

    @NotNull(message = "General Information is required")
    @Valid
    @JsonProperty("GeneralInformation")
    private GeneralInformationRequest generalInformation;

    @NotNull(message = "Education Details is required")
    @Valid
    @JsonProperty("EducationDetails")
    private EducationDetailsRequest educationDetails;

    @Valid
    @JsonProperty("CareerInterests")
    private CareerInterestsRequest careerInterests;

    @Valid
    @JsonProperty("SkillsAndStrengths")
    private SkillsAndStrengthsRequest skillsAndStrengths;

    @Valid
    @JsonProperty("HobbiesAndInterests")
    private HobbiesAndInterestsRequest hobbiesAndInterests;

    @Valid
    @JsonProperty("AchievementsAndCertifications")
    private AchievementsAndCertificationsRequest achievementsAndCertifications;

    @Valid
    @JsonProperty("EntranceExams")
    private EntranceExamsRequest entranceExams;
}
