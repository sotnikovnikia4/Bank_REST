package com.example.bankcards.config;

import com.example.bankcards.exception.ExceptionController;
import com.example.bankcards.security.JWTFilter;
import com.example.bankcards.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig{

    private final JWTFilter jwtFilter;
    private final ExceptionController exceptionController;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        "/error",
                                        "/api/auth/login"
                                )
                                .permitAll()
                                .requestMatchers(
                                        "/api/auth/registration",
                                        "/api/cards/create",
                                        "/api/cards/update",
                                        "/api/cards/get-all",
                                        "/api/cards/delete"
                                ).hasRole(AuthorizationService.ADMIN_ROLE)
                                .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(m ->
                        m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> {
                    exception.accessDeniedHandler(exceptionController::accessDenied);
                    exception.authenticationEntryPoint(exceptionController::entryPoint);
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };
    }
}
