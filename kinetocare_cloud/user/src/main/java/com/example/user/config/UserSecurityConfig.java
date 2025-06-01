package com.example.user.config;

import com.example.common.security.config.JwtSecurityConfig;
import com.example.common.security.filter.JwtAuthFilter;
import com.example.user.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class UserSecurityConfig extends JwtSecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    public UserSecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        super(jwtAuthFilter);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configureMicroserviceSpecificRules(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/port").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(super.passwordEncoder());
        return provider;
    }
}
