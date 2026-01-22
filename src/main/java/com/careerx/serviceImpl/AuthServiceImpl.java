package com.careerx.serviceImpl;



import com.careerx.apirequests.AuthRequest;
import com.careerx.apirequests.ForgotPasswordRequest;
import com.careerx.apirequests.ResetPasswordRequest;
import com.careerx.apirequests.VerifyOtpRequest;
import com.careerx.apiresponses.ApiResponse;
import com.careerx.apiresponses.AuthResponse;
import com.careerx.entities.OTP;
import com.careerx.entities.Users;
import com.careerx.exception.BadRequestException;
import com.careerx.exception.NotFoundException;
import com.careerx.exception.UnauthorizedException;
import com.careerx.repository.OtpRepository;
import com.careerx.repository.UserRepository;
import com.careerx.services.AuthService;
import com.careerx.services.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository usersRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    public AuthResponse login(AuthRequest request) {

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getName(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .userName(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();
    }

    @Override
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {

        usersRepository.findByEmail(request.getEmail()).ifPresent(user -> {

            // Remove old OTPs
            otpRepository.deleteAll(otpRepository.findByEmailAndIsUsedFalse(request.getEmail()));

            String otp = String.valueOf(100000 + new Random().nextInt(900000));

            OTP otpRecord = OTP.builder()
                    .email(request.getEmail())
                    .otpCode(otp)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .isUsed(false)
                    .build();

            otpRepository.save(otpRecord);

            emailService.sendOTPEmail(request.getEmail(), otp);
        });

        return new ApiResponse("If the email exists, an OTP has been sent.", request);
    }

    @Override
    public ApiResponse verifyOtp(VerifyOtpRequest request) {

        OTP otpRecord = otpRepository
                .findFirstByEmailAndOtpCodeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        request.getEmail(), request.getOtpCode(), LocalDateTime.now()
                )
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP."));

        return new ApiResponse("OTP verified successfully.", otpRecord);
    }

    @Override
    public ApiResponse resetPassword(ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match.");
        }

        OTP otpRecord = otpRepository
                .findFirstByEmailAndOtpCodeAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        request.getEmail(), request.getOtpCode(), LocalDateTime.now()
                )
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP."));

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found."));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);

        otpRecord.setUsed(true);
        otpRepository.save(otpRecord);

        return new ApiResponse("Password reset successfully.", user);
    }

	

}
