package com.kelikatheya.microservices.userRegistration.services.impls;

import com.kelikatheya.microservices.userRegistration.entity.*;
import com.kelikatheya.microservices.userRegistration.model.UserModel;
import com.kelikatheya.microservices.userRegistration.requestModels.UserRequest;
import com.kelikatheya.microservices.userRegistration.services.EmailSenderService;
import com.kelikatheya.microservices.userRegistration.services.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    Environment env;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public UserModel registerUser(UserRequest userRequest) {
            UserEntity existingUser = userRepository.findByEmail(userRequest.getEmail());
            if(existingUser!=null){
                System.out.println("Already existing User");
                return null;
            }else {
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                //map fields from userRequest to userEntity
                UserEntity userEntity = modelMapper.map(userRequest , UserEntity.class);
                userEntity = modelMapper.map(userRequest, UserEntity.class);
                userEntity.setUserId(UUID.randomUUID().toString());
                userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
                mongoTemplate.save(userEntity);
                ConfirmationToken confirmationToken = new ConfirmationToken(userEntity);
                confirmationTokenRepository.save(confirmationToken);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(userEntity.getEmail());
                mailMessage.setSubject("Complete Registration!");
                mailMessage.setFrom("chand312902@gmail.com");
                mailMessage.setText("To confirm your account, please click here : "
                        +"http://localhost:8082/confirm-account?token="+confirmationToken.getConfirmationToken());
                emailSenderService.sendEmail(mailMessage);
                UserModel userModel = modelMapper.map(userEntity, UserModel.class);
                return userModel;
            }

    }

    @Override
    public UserEntity getUserDetailsByEmail(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        return userEntity;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(userName);
        if(userEntity == null) {
            throw new UsernameNotFoundException(userName);
        }
        return new User(userEntity.getEmail() , userEntity.getEncryptedPassword() , true , true ,true , true , new ArrayList<>());
    }

    @Override
    public void resetPasswordToken(HttpServletRequest request , String userEmail) {
        UserEntity user = getUserDetailsByEmail(userEmail);
        if (user == null) {
            System.out.println("User not found");
        }
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        mailSender.send(constructResetTokenEmail(getAppUrl(request),
                request.getLocale(), token, user));
    }

    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }
    private SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, UserEntity user) {
        String url = contextPath + "/user/changePassword?token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                             UserEntity user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    @Override
    public void changeUserPassword(UserEntity user, String password) {
        user.setEncryptedPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token) .getUser());
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
