package com.ksugp.MMService.controller;

import com.ksugp.MMService.model.*;
import com.ksugp.MMService.service.AuthService;
import com.ksugp.MMService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/mobile")
public class MobileAppController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final AuthService authService;
    public MobileAppController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    //запрос авторизации
    @GetMapping("/login")
    public SafeUser mobileAuth(HttpServletRequest request) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail(email);
        dto.setPassword(password);
        SafeUser currentSUser = new SafeUser();
        try {
            String token = authService.createJwtToken(dto);
            currentSUser = authService.getJwtTokenProvider().getFullClaimsInfoInSafeUserClass(token);
            currentSUser.setToken(token);
            return currentSUser;
        } catch (AuthenticationException e) {
            return currentSUser;
        }
    }

    //запрос на отправку своих координат
    @GetMapping("/send/coordinates")
    @PreAuthorize("hasAuthority('users:read')")
    public SaveResponse getCoordinates(HttpServletRequest request) {
        String token = request.getParameter("token");
        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");
        String timestamp = request.getParameter("timestamp");
        System.out.println("Полученная долгота: "+latitude);
        System.out.println("Полученная широта: "+longitude);
        System.out.println("Полученный timestamp: "+timestamp);
        SafeUser currentSUser =  authService.getJwtTokenProvider().getFullClaimsInfoInSafeUserClass(token);
        User currentUser = userService.getUser(currentSUser.getId()).get();
        System.out.println("Геоданные от пользователя: "+currentUser.getEmail());
        currentUser.setLatitude(Double.parseDouble(latitude));
        currentUser.setLongitude(Double.parseDouble(longitude));
        currentUser.setTimestamp(Long.valueOf(timestamp));
        userService.updateUser(currentUser, currentUser.getId());
        SaveResponse resp = new SaveResponse();
        resp.setStatus("OK");
        return resp;
    }

    //запрос на получение всех координат(админ)
    @GetMapping("/get/updated/users")
    @PreAuthorize("hasAuthority('users:write')")
    public UsersResponse getUpdatedUsers(){
        List<User> allUsers = userService.getAllUsers();
        List<UserResponse> respUsersList = new ArrayList<>();
        for(User buf : allUsers){
            buf.setMid(buf.getId().intValue());
            UserResponse userResponse = new UserResponse();
            userResponse.setMid(String.valueOf(buf.getMid()));
            userResponse.setUsername(buf.getUsername());
            userResponse.setEmail(buf.getEmail());
            userResponse.setStatus(buf.getStatus());
            userResponse.setRole(buf.getRole().toString());
            userResponse.setInfo(buf.getInfo());
            userResponse.setLatitude(String.valueOf(buf.getLatitude()));
            userResponse.setLongitude(String.valueOf(buf.getLongitude()));
            userResponse.setTimestamp(String.valueOf(buf.getTimestamp().intValue()));
            userResponse.setColor(buf.getColor());
            respUsersList.add(userResponse);
        }
        UsersResponse resp = new UsersResponse();
        resp.setAllUsers(respUsersList);
        return resp;
    }
}