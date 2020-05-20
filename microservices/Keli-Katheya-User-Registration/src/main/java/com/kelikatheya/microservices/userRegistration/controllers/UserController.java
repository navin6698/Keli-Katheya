package com.kelikatheya.microservices.userRegistration.controllers;

import com.kelikatheya.microservices.userRegistration.model.UserModel;
import com.kelikatheya.microservices.userRegistration.requestModels.UserRequest;
import com.kelikatheya.microservices.userRegistration.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    Environment environment;

    @Autowired
    UserService userService;

    @RequestMapping(value="/status", method = RequestMethod.GET)
    public String checkStatus() {
        return "User Registration app working on port number :" + environment.getProperty("local.server.port");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE} , consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserModel> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserModel userModel = userService.registerUser(userRequest);
        return new ResponseEntity<UserModel>(userModel , HttpStatus.CREATED);
    }
}
