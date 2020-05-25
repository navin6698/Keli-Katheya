package com.kelikatheya.microservices.userRegistration.entity;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    public PasswordResetToken(String token, UserEntity user) {
        this.token = token;
        this.user = user;
    }

    @Id
    private Long id;
    private String token;
    private UserEntity user;
    private Date expiryDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
