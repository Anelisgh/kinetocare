package com.example.evaluare.config;

import com.example.common.security.config.JwtSecurityConfig;
import com.example.common.security.filter.JwtAuthFilter;
import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class EvaluareSecurityConfig extends JwtSecurityConfig {

    public EvaluareSecurityConfig(JwtAuthFilter jwtAuthFilter) {
        super(jwtAuthFilter);
    }

    @Override
    protected void configureMicroserviceSpecificRules(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/evaluare/adaugare").hasRole("TERAPEUT")
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
        );
    }
}

