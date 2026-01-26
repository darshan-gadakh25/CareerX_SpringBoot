package com.careerx.apirequests;

import com.careerx.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GeneralInformationRequest {

    @NotNull(message = "Date of Birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Mobile Number is required")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "Mobile Number must be between 9 and 12 digits")
    private String mobileNumber;

    private String preferredLanguage;
}
