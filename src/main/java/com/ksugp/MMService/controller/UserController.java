package com.ksugp.MMService.controller;

import com.ksugp.MMService.entity.User;
import com.ksugp.MMService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service")
public class UserController {
    @Autowired
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/show")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/show/{userId}")
    public Optional<User> getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }
    @PostMapping("/add")
    public void createUser(@RequestBody User user){
        userService.saveUser(user);
    }
    @DeleteMapping("/delete/{userId}")
    public void deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
    }
    @PutMapping("/update/{userId}")
    public void updateUser(@RequestBody User user, @PathVariable Long userId){
        userService.updateUser(user,userId);
    }
}
