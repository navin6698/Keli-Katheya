package com.kelikatheya.microservices.userRegistration.controllers;

import com.kelikatheya.microservices.userRegistration.requestModels.ChangePassword;
import com.kelikatheya.microservices.userRegistration.entity.UserEntity;
import com.kelikatheya.microservices.userRegistration.model.GenericResponse;
import com.kelikatheya.microservices.userRegistration.model.UserModel;
import com.kelikatheya.microservices.userRegistration.requestModels.UserRequest;
import com.kelikatheya.microservices.userRegistration.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    Environment environment;

    @Autowired
    UserService userService;

    @Autowired
    private MessageSource messages;

    @RequestMapping(value="/status", method = RequestMethod.GET)
    public String checkStatus() {
        return "User Registration app working on port number :" + environment.getProperty("local.server.port");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE} , consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserModel> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserModel userModel = userService.registerUser(userRequest);
        return new ResponseEntity<UserModel>(userModel , HttpStatus.CREATED);
    }

    @RequestMapping(value ="/resetPassword" , method = RequestMethod.POST)
    public GenericResponse resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) {

        userService.resetPasswordToken(request , userEmail);
        return new GenericResponse(
                messages.getMessage("message.resetPasswordEmail", null,
                        request.getLocale()));
    }

    @GetMapping("=/changePassword")
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token) {
        String result = userService.validatePasswordResetToken(token);
        if(result != null) {
            String message = messages.getMessage("auth.message." + result, null, locale);
            return "redirect:/login.html?lang="
                    + locale.getLanguage() + "&message=" + message;
        } else {
            model.addAttribute("token", token);
            return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
        }
    }

    @PostMapping("/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid ChangePassword password) {

        String result = userService.validatePasswordResetToken(password.getToken());

        if(result != null) {
            return new GenericResponse(messages.getMessage(
                    "auth.message." + result, null, locale));
        }

        Optional<UserEntity> user = userService.getUserByPasswordResetToken(password.getToken());
        if(user.isPresent()) {
            userService.changeUserPassword(user.get(), password.getNewPassword());
            return new GenericResponse(messages.getMessage(
                    "message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage(
                    "auth.message.invalid", null, locale));
        }
    }

}
