package com.ksugp.MMService.controller;

import com.ksugp.MMService.entity.AuthRequestDTO;
import com.ksugp.MMService.entity.User;
import com.ksugp.MMService.service.AuthService;
import com.ksugp.MMService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final AuthService authService;
    @Autowired
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginFormPage";
    }
    @PostMapping("/login")
    public String authenticate(@ModelAttribute("authData") AuthRequestDTO requestObj, HttpServletResponse response) {
//    public ResponseEntity<?> authenticate(@RequestBody AuthRequestDTO requestObj){ //для Rest controller
        try {
            String token = authService.createJwtToken(requestObj);
            authService.setAuthCookie(response, token);
//            Map<Object,Object> response = new HashMap<>(); //для Rest controller
//            response.put("email", requestObj.getEmail());
//            response.put("token",token);
//            return ResponseEntity.ok(response);
            return "loginSuccess";
        } catch (AuthenticationException e) {
            return "loginError";
        }
    }
    @GetMapping("/signup")
    public String showSignupPage() {
        return "signupFormPage";
    }
    @PostMapping("/signup")
    public String signup(@ModelAttribute("signupUserInfo") User user) {
        if(userService.signupUser(user)){return "signupSuccess";}
        else{return "signupError";}
    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler(); //нужно разобраться
        securityContextLogoutHandler.logout(request,response,null);

        boolean isAuth = authService.checkAuthCookie(request);
        if(isAuth) {
            authService.deleteAuthCookie(response);
            return "logoutSuccess";
        }else{
            return "logoutError";
        }
    }
}
