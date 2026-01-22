package com.careerx.services;

import com.careerx.apirequests.AuthRequest;
import com.careerx.apirequests.ForgotPasswordRequest;
import com.careerx.apirequests.ResetPasswordRequest;
import com.careerx.apirequests.VerifyOtpRequest;
import com.careerx.apiresponses.ApiResponse;
import com.careerx.apiresponses.AuthResponse;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    ApiResponse forgotPassword(ForgotPasswordRequest request);

    ApiResponse verifyOtp(VerifyOtpRequest request);

    ApiResponse resetPassword(ResetPasswordRequest request);
}
