package com.ksugp.MMService.config;

import com.ksugp.MMService.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/").permitAll()
//                .requestMatchers(HttpMethod.GET,"/service/**").hasAnyRole(Role.ADMIN.name(),Role.USER.name()) без пермишнов
//                .requestMatchers(HttpMethod.POST,"/service/**").hasRole(Role.ADMIN.name())
//                .requestMatchers(HttpMethod.DELETE,"/service/**").hasRole(Role.ADMIN.name())
//                .requestMatchers(HttpMethod.PUT,"/service/**").hasRole(Role.ADMIN.name())
//                .requestMatchers(HttpMethod.GET,"/service/**").hasAuthority(Permission.USERS_READ.getPermission()) без UserController/@PreAuthorize и SecurityConfig/@EnableMethodSecurity
//                .requestMatchers(HttpMethod.POST,"/service/**").hasAuthority(Permission.USERS_WRITE.getPermission())
//                .requestMatchers(HttpMethod.DELETE,"/service/**").hasAuthority(Permission.USERS_WRITE.getPermission())
//                .requestMatchers(HttpMethod.PUT,"/service/**").hasAuthority(Permission.USERS_WRITE.getPermission())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/auth/login").permitAll()
                .defaultSuccessUrl("/auth/success")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/auth/login");


        return http.build();
    }
    @Bean
    protected UserDetailsService userDetailsService(){
        return new InMemoryUserDetailsManager(
                User.builder().
                        username("admin").
                        password(passwordEncoder().encode("admin"))
//                        .roles(Role.ADMIN.name()) без пермишнов
                        .authorities(Role.ADMIN.getAuthorities())
                        .build(),
                User.builder().
                        username("user").
                        password(passwordEncoder().encode("user"))
//                        .roles(Role.USER.name())
                        .authorities(Role.USER.getAuthorities())
                        .build()
        );
    }
    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}
