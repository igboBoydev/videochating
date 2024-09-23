package com.abel.videochattingsystem.Security;


import com.abel.videochattingsystem.Config.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    public static final String[] AUTHENTICATED_URLS = { "/ws/video-call", "/api/v1/users/**", "/api/v1/login/**" };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF if needed for stateless APIs
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTHENTICATED_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            Map<String, Object> responseMap = new HashMap<>();
                            ObjectMapper mapper = new ObjectMapper();
                            response.setStatus(401);
                            responseMap.put("error", true);
                            responseMap.put("message", authException.getMessage());
                            response.setHeader("content-type", "application/json");
                            String responseMsg = mapper.writeValueAsString(responseMap);
                            response.getWriter().write(responseMsg);
                        })
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
