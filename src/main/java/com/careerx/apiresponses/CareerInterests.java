package com.careerx.apiresponses;

import com.careerx.enums.CareerInterest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CareerInterests {
    private CareerInterest areasOfInterest;
    private String preferredCareerDomain;
    private String dreamJobOrRole;
}
