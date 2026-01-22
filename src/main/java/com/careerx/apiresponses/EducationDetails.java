package com.careerx.apiresponses;

import com.careerx.enums.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationDetails {
    private EducationLevel currentEducationLevel;
    private String boardOrUniversity;
    private String schoolOrCollegeName;
    private Streams stream;
    private String currentYearOrSemester;
    private Double overallPercentageOrCGPA;

    private Boolean hasTakenGapYear;
    private Integer numberOfGapYears;
    private GapReason reasonForGap;
}
