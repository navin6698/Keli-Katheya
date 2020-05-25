package com.kelikatheya.microservices.userRegistration.services;

import com.kelikatheya.microservices.userRegistration.entity.UserEntity;
import com.kelikatheya.microservices.userRegistration.model.UserModel;
import com.kelikatheya.microservices.userRegistration.requestModels.UserRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    UserModel registerUser(UserRequest userRequest);
    UserEntity getUserDetailsByEmail(String email);
    void resetPasswordToken(HttpServletRequest request,String userEmail);
    public String validatePasswordResetToken(String token);
    public void changeUserPassword(UserEntity user, String password);
    Optional<UserEntity> getUserByPasswordResetToken(final String token);
}
