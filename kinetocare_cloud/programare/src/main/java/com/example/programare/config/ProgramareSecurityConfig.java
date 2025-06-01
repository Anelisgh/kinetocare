package com.example.programare.config;

import com.example.common.security.config.JwtSecurityConfig;
import com.example.common.security.filter.JwtAuthFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class ProgramareSecurityConfig extends JwtSecurityConfig {

    public ProgramareSecurityConfig(JwtAuthFilter jwtAuthFilter) {
        super(jwtAuthFilter);
    }

    @Override
    protected void configureMicroserviceSpecificRules(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
        );
    }
}
