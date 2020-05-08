package com.kelikatheya.microservices.userRegistration.services;

import com.kelikatheya.microservices.userRegistration.entity.UserEntity;
import com.kelikatheya.microservices.userRegistration.model.UserModel;
import com.kelikatheya.microservices.userRegistration.requestModels.UserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserModel registerUser(UserRequest userRequest);
    UserEntity getUserDetailsByEmail(String email);
}
