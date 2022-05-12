package com.geekub.cinema.web.configs;

import com.geekhub.user.UserRepository;
import com.geekub.cinema.web.auth.LoginPasswordAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class CommonSecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginPasswordAuthenticationProvider loginPasswordAuthentication
            (BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        return new LoginPasswordAuthenticationProvider(bCryptPasswordEncoder, userRepository);
    }
}
