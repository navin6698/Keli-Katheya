package com.kelikatheya.microservices.userRegistration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelikatheya.microservices.userRegistration.entity.UserEntity;
import com.kelikatheya.microservices.userRegistration.requestModels.UserLoginRequest;
import com.kelikatheya.microservices.userRegistration.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    public AuthenticationFilter(UserService userService , Environment environment , AuthenticationManager authenticationManager){
        this.userService = userService;
        this.environment = environment;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse) {
        try {
            UserLoginRequest creds = new ObjectMapper().readValue(httpServletRequest.getInputStream(), UserLoginRequest.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail() , creds.getPassword() , new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void successfulAuthentication(HttpServletRequest httpServletRequest ,
                                         HttpServletResponse httpServletResponse ,
                                         FilterChain filterChain,
                                         Authentication authentication) throws IOException , ServletException {
        String email = ((User)authentication.getPrincipal()).getUsername();
        UserEntity userEntity = userService.getUserDetailsByEmail(email);
        String jwtToken = Jwts.builder()
                .setSubject(userEntity.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512 , environment.getProperty("token.secret"))
                .compact();
        httpServletResponse.addHeader("JWTtoken" , jwtToken);
        httpServletResponse.addHeader("Email" , userEntity.getEmail());
    }
}
