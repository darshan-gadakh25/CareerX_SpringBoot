package com.careerx.services;

import com.careerx.apirequests.StudentProfileRequest;
import com.careerx.apiresponses.StudentProfileResponse;

public interface StudentProfileService {

    StudentProfileResponse createStudentProfile(Long userId, StudentProfileRequest request);

    StudentProfileResponse getStudentProfile(Long userId);

    StudentProfileResponse getStudentProfileById(Long studentId);

    StudentProfileResponse updateStudentProfile(Long userId, StudentProfileRequest request);

    boolean deleteStudentProfile(Long userId);

    String uploadProfilePicture(Long userId, org.springframework.web.multipart.MultipartFile file);
}