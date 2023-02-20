package com.ksugp.MMService.service;

import com.ksugp.MMService.Security.JwtTokenProvider;
import com.ksugp.MMService.entity.AuthRequestDTO;
import com.ksugp.MMService.entity.User;
import com.ksugp.MMService.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    @Value("${jwt.cookieName}")
    private String cookieName;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    public JwtTokenProvider getJwtTokenProvider() {
        return jwtTokenProvider;
    }
    public String createJwtToken(AuthRequestDTO requestObj) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestObj.getEmail(), requestObj.getPassword()));
        User user = userRepository.findByEmail(requestObj.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
        return jwtTokenProvider.createToken(requestObj.getEmail(), user.getRole().name(), user.getUsername(), user.getId());
    }

    public void setAuthCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    public void deleteAuthCookie(HttpServletResponse response){
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public boolean checkAuthCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        boolean isAuthCooke = false;
        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(cookieName.equals(c.getName())) {
                    isAuthCooke = true;
                    break;
                }
            }
        }
        return isAuthCooke;
    }
//    public String getEmail (String token){
//        return jwtTokenProvider.getUsername(token);
//    }
}
