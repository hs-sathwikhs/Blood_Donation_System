package com.blooddonation.config;

import com.blooddonation.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self'; font-src 'self'; connect-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'; form-action 'self'")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/organiser", "/donor", "/hospital", "/inventory", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**", "/favicon.svg").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/camps/*/registrations").hasRole("ORGANISER")
                        .requestMatchers(HttpMethod.GET, "/api/camps", "/api/camps/*").permitAll()
                        .requestMatchers("/api/camps/**").hasRole("ORGANISER")
                        .requestMatchers("/api/donors/**").hasRole("DONOR")
                        .requestMatchers("/api/hospitals/**").hasRole("HOSPITAL")
                        .requestMatchers(HttpMethod.GET, "/api/inventory/stock").authenticated()
                        .requestMatchers("/api/inventory/**").hasRole("ADMIN")
                        .anyRequest().denyAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(401); res.setContentType("application/json");
                            res.getWriter().write("{\"message\":\"Sign in to continue\"}");
                        })
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(403); res.setContentType("application/json");
                            res.getWriter().write("{\"message\":\"This action is not available for your role\"}");
                        }));
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
