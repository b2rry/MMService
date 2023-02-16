package com.ksugp.MMService.controller;

import com.ksugp.MMService.Security.JwtTokenProvider;
import com.ksugp.MMService.entity.AuthRequestDTO;
import com.ksugp.MMService.entity.User;
import com.ksugp.MMService.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.cookieName}")
    private String cookieName;

    public AuthRestController(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("login")
    public String showLoginPage() {
        return "login";
    }
    @PostMapping("/login")
    //public ResponseEntity<?> authenticate(@RequestBody AuthRequestDTO request){
    public ResponseEntity<?> authenticate(@ModelAttribute("authData") AuthRequestDTO request, HttpServletResponse httpResponse){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
            String token = jwtTokenProvider.createToken(request.getEmail(), user.getRole().name());
            Map<Object,Object> response = new HashMap<>();
            response.put("email", request.getEmail());
            response.put("token",token);

            Cookie cookie = new Cookie(cookieName, token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            httpResponse.addCookie(cookie);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.FORBIDDEN);
        }
    }
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request,response,null);
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
