package com.careerx.apirequests;

import com.careerx.enums.UserRole;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "Password must contain at least one letter, one number, and one special character"
    )
    private String password;

    @NotBlank
    @Email(message = "Invalid Email Address")
    private String email;

    @Min(value = 1, message = "Age must be between 1 and 100")
    @Max(value = 100, message = "Age must be between 1 and 100")
    private int age;

    private UserRole role;

    @NotBlank(message = "Location is required")
    private String location;
}