package com.careerx.apiresponses;

import com.careerx.enums.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillsAndStrengths {
    private TechnicalSkill technicalSkills;
    private SoftSkill softSkills;
    private SkillLevel skillLevel;
}
