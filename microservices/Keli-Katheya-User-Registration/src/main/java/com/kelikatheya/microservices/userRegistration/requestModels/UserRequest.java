package com.kelikatheya.microservices.userRegistration.requestModels;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRequest {
    @NotNull(message = "Name can't be empty")
    @Size(min = 5 , message = "Minimum of 5 Characters required")
    private String name;

    @NotNull(message = "email can't be empty")
    @Email
    private String email;

    @NotNull(message = "password can't be empty")
    @Size(min = 6 , max = 15 , message = "Password size should be between 5 and 15")
    private String password;

    public String getName() {
        return name;
    }

    public void settName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
