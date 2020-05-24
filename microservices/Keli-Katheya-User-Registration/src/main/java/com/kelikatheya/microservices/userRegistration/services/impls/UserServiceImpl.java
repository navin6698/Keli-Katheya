package com.kelikatheya.microservices.userRegistration.services.impls;

import com.kelikatheya.microservices.userRegistration.entity.ConfirmationToken;
import com.kelikatheya.microservices.userRegistration.entity.ConfirmationTokenRepository;
import com.kelikatheya.microservices.userRegistration.entity.UserEntity;
import com.kelikatheya.microservices.userRegistration.entity.UserRepository;
import com.kelikatheya.microservices.userRegistration.model.UserModel;
import com.kelikatheya.microservices.userRegistration.requestModels.UserRequest;
import com.kelikatheya.microservices.userRegistration.services.EmailSenderService;
import com.kelikatheya.microservices.userRegistration.services.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;


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
}
