package com.careerx.apiresponses;

import com.careerx.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GeneralInformation {
    private String name;
    private String email;
    private String location;
    private Integer age;

    private LocalDateTime dateOfBirth;
    private Gender gender;
    private String mobileNumber;
    private String preferredLanguage;
}
