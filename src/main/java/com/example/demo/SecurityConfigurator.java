package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfigurator {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private TokenFilter tokenFilter;

    @Autowired
    public void setTokenFilter(TokenFilter filter) {
        tokenFilter = filter;
    }

    public SecurityConfigurator() {
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //@Bean
    //public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
    //        throws Exception {
    //    return authenticationConfiguration.getAuthenticationManager();
    //}

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    //@Bean
    //@Primary
    //public AuthenticationManagerBuilder configureAuthenticationManagerBuilder(AuthenticationManagerBuilder auth)
    //        throws Exception {
    //    auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    //    return auth;
    //}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(httpServletRequest -> {
                return new CorsConfiguration().applyPermitDefaultValues();
            });
        }).exceptionHandling(exceptionHandlingConfigurer -> {
            System.out.println("exceptionHandlingConfigurer");
            exceptionHandlingConfigurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        }).sessionManagement(sessionManagementConfigurer -> {
            sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }).authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/secured/user").authenticated()
                    .anyRequest().permitAll();
        }).addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
