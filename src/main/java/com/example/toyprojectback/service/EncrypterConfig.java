package com.example.toyprojectback.service;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class EncrypterConfig {

    @Bean
    public EncrypterConfig encoder(){
        return new EncrypterConfig();
    }
}
