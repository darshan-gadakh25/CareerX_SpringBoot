package com.careerx.apiresponses;


import com.careerx.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    private UserRole role;
    private String message;
}
