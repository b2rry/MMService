package com.ksugp.MMService.config;

import com.ksugp.MMService.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/").permitAll()
                .requestMatchers(HttpMethod.GET,"/service/**").hasAnyRole(Role.ADMIN.name(),Role.USER.name())
                .requestMatchers(HttpMethod.POST,"/service/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE,"/service/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/service/**").hasRole(Role.ADMIN.name())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

        return http.build();
    }
    @Bean
    protected UserDetailsService userDetailsService(){
        return new InMemoryUserDetailsManager(
                User.builder().
                        username("admin").
                        password(passwordEncoder().encode("admin"))
                        .roles(Role.ADMIN.name())
                        .build(),
                User.builder().
                        username("user").
                        password(passwordEncoder().encode("user"))
                        .roles(Role.USER.name())
                        .build()
        );
    }
    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}
