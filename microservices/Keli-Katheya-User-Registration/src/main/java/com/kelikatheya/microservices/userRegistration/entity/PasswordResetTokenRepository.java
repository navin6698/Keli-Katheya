package com.kelikatheya.microservices.userRegistration.entity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUser(UserEntity user);
}