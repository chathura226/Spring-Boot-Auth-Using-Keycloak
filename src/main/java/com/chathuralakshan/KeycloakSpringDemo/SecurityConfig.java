package com.chathuralakshan.KeycloakSpringDemo;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //to enable method wise security lke preAuthorizations
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConvertor jwtAuthConvertor;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //authenticating every request with oauth2resource server which is defined in applicaiton.properties
        httpSecurity
                .authorizeHttpRequests(authorize->authorize
                        .anyRequest()
                        .authenticated()
                ).oauth2ResourceServer((oauth2)->oauth2.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConvertor)))//using custom convetor
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //session management policy
        // stateless - spring security will not create or use any session

        return httpSecurity.build();
    }
}
