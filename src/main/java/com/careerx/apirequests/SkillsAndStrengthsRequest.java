package com.careerx.apirequests;

import com.careerx.enums.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillsAndStrengthsRequest {
    private TechnicalSkill technicalSkills;
    private SoftSkill softSkills;
    private SkillLevel skillLevel;
}
