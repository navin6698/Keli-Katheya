package com.kelikatheya.microservices.userRegistration.entity;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "User")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = -5443144337747885152L;
    @Id
    @GeneratedValue
    private Long id; // DB id
    @Column(nullable = false , length = 50)
    private String name;
    @Column(nullable = false , length = 100 , unique = true)
    private String email;
    @Column(nullable = false , length = 100 , unique = true)
    private String userId;
    @Column(nullable = false , length = 100 , unique = true)
    private String encryptedPassword;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
