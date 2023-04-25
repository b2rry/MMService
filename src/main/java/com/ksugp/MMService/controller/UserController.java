package com.ksugp.MMService.controller;

import com.ksugp.MMService.model.User;
import com.ksugp.MMService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/settings")
public class UserController {
    @Autowired
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/show")
    @PreAuthorize("hasAuthority('users:writeplus')")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/show/{userId}")
    @PreAuthorize("hasAuthority('users:writeplus')")
    public Optional<User> getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('users:writeplus')")
    public void createUser(@RequestBody User user){
        userService.saveUser(user);
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('users:writeplus')")
    public void deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAuthority('users:writeplus')")
    public void updateUser(@RequestBody User user, @PathVariable Long userId){
        userService.updateUser(user,userId);
    }
}