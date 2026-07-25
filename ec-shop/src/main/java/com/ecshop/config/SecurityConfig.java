package com.ecshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // BUG #6 (HIGH): Session fixation protection is disabled
            // .sessionManagement().sessionFixation().migrateSession() is the secure default
            // Without it, an attacker can fixate a session ID and hijack the session after login
            .sessionManagement(session -> session
                .sessionFixation(sessionFixation -> sessionFixation.none()) // BUG: should be .migrateSession()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            // BUG #6 continued: CSRF disabled without proper consideration
            // While CSRF is sometimes disabled for REST APIs, combined with session fixation
            // vulnerability, this makes the attack surface larger
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
