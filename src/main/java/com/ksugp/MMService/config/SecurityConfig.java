package com.ksugp.MMService.config;

import com.ksugp.MMService.Security.JwtConfigurer;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{
//    @Autowired
//    private final UserDetailsService userDetailsService;
//
//    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }

    @Autowired
    private final JwtConfigurer jwtConfigurer;

    public SecurityConfig(JwtConfigurer jwtConfigurer) {
        this.jwtConfigurer = jwtConfigurer;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/*").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/auth/login").permitAll()
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
                .apply(jwtConfigurer);
//                .formLogin()
//                .loginPage("/auth/login").permitAll()
//                .defaultSuccessUrl("/auth/success", true)
//                .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
 //               .deleteCookies("JSESSIONID")
//                .logoutSuccessUrl("/");

        return http.build();
    }

//    @Bean
//    protected UserDetailsService userDetailsService(){
//        return new InMemoryUserDetailsManager(
//                User.builder().
//                        username("admin").
//                        password(passwordEncoder().encode("admin"))
////                        .roles(Role.ADMIN.name()) без пермишнов
//                        .authorities(Role.ADMIN.getAuthorities())
//                        .build(),
//                User.builder().
//                        username("user").
//                        password(passwordEncoder().encode("user"))
////                        .roles(Role.USER.name())
//                        .authorities(Role.USER.getAuthorities())
//                        .build()
//        );
//    }
    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

//    @Bean
//    public AuthenticationManager authenticationManager(){ удалить
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(daoAuthenticationProvider);
//    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
//    @Bean
//    public Authentic
//    ationManager authenticationManager(){ //источник - https://stackoverflow.com/questions/74877743/spring-security-6-0-dao-authentication
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(daoAuthenticationProvider);
//    }
}
