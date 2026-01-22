package com.careerx.apiresponses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntranceExams {
    private Boolean appearedForCompetitiveExams;
    private String examName;
    private String examScoreOrRank;
    private Integer examYear;
}
