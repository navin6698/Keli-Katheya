package com.kelikatheya.microservices.userRegistration.security;

import com.kelikatheya.microservices.userRegistration.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class UserSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserService userService;

    @Autowired
    Environment environment;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //cross site request forgery since we use JWT token for authorize user
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**").permitAll()
                .and()
                .addFilter(getAuhtenticationFilter());
    }
    private AuthenticationFilter getAuhtenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService , environment , authenticationManager());
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("authenticate.url"));
        return  authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        //which service is used to load login details and which password encoder is used
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
