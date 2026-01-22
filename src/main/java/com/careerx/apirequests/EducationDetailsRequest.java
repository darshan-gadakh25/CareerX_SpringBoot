package com.careerx.apirequests;

import com.careerx.enums.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationDetailsRequest {

    @NotNull(message = "Current Education Level is required")
    private EducationLevel currentEducationLevel;

    private String boardOrUniversity;
    private String schoolOrCollegeName;
    private Streams streams;
    private String currentYearOrSemester;
    private Double overallPercentageOrCGPA;

    private Boolean hasTakenGapYear;
    private Integer numberOfGapYears;
    private GapReason reasonForGap;
}
