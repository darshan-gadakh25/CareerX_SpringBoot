package com.careerx.services;

import com.careerx.apirequests.UserRequest;
import com.careerx.apiresponses.UserResponse;
import com.careerx.entities.Users;

import jakarta.mail.MessagingException;

import java.util.List;

public interface UserService {

    List<Users> getAllUsers();

    Users getUserById(Long id);

    UserResponse createUser(UserRequest request) throws MessagingException;

    String updateUser(Long id, Users user);

    void deleteUser(Long id);
}