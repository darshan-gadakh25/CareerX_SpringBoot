package com.careerx.apirequests;

import com.careerx.enums.CareerInterest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CareerInterestsRequest {
    private CareerInterest areasOfInterest;
    private String preferredCareerDomain;
    private String dreamJobOrRole;
}
