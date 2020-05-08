package com.kelikatheya.microservices.userRegistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(scanBasePackages={
		"com.kelikatheya.microservices"})
public class KeliKatheyaUserRegistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeliKatheyaUserRegistrationApplication.class, args);

	}
	//object needs to exist in application context to inject to service
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
