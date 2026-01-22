package com.careerx.serviceImpl;

import com.careerx.apirequests.UserRequest;
import com.careerx.apiresponses.UserResponse;
import com.careerx.entities.Users;
import com.careerx.enums.UserRole;
import com.careerx.exception.DuplicateResourceException;
import com.careerx.exception.ResourceNotFoundException;
import com.careerx.repository.UserRepository;
import com.careerx.services.EmailService;
import com.careerx.services.UserService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
     private final EmailService emailService;  // optional

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Exist with ID " + id));
    }

    @Override
    public UserResponse createUser(UserRequest request) throws MessagingException {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered.");
        }

        Users user = Users.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .age(request.getAge())
                .role(request.getRole() != null ? request.getRole() : UserRole.STUDENT)
                .location(request.getLocation())
                .build();

        Users savedUser = userRepository.save(user);

        // Optional: send email
         emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getName());

        return UserResponse.builder()
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .message("User registered successfully. Please login to continue.")
                .build();
    }

    @Override
    public String updateUser(Long id, Users user) {
        Users existingUser = getUserById(id);

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        existingUser.setRole(user.getRole());
        existingUser.setLocation(user.getLocation());

        userRepository.save(existingUser);

        return "User Update Successfully with Id " + id;
    }

    @Override
    public void deleteUser(Long id) {
        Users user = getUserById(id);
        userRepository.delete(user);
    }
}
