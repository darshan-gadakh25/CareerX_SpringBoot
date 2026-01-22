package com.careerx.apirequests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntranceExamsRequest {
    private Boolean appearedForCompetitiveExams;
    private String examName;
    private String examScoreOrRank;
    private Integer examYear;
}
