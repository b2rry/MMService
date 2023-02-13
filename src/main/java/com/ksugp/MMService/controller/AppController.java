package com.ksugp.MMService.controller;

import com.ksugp.MMService.entity.SafeOutputUser;
import com.ksugp.MMService.entity.User;
import com.ksugp.MMService.service.UserService;
import org.apache.catalina.startup.SafeForkJoinWorkerThreadFactory;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/service")
public class AppController {
    @Autowired
    private final UserService userService;

    public AppController(UserService userService) {
        this.userService = userService;
    }
    @PreAuthorize("hasAuthority('users:read')")
    @GetMapping
    public String getAllActions(Model model) {
        List<User> usersList = userService.getAllUsers();
        int num = 1;
        List<SafeOutputUser> sUsers = new ArrayList<>();
        for(User user : usersList){
            sUsers.add(makeSafeUser(user, num));
            num++;
        }
        model.addAttribute("usersList",sUsers);
        return "service";
    }
    @PreAuthorize("hasAuthority('users:read')")
    @GetMapping("/users")
    public String showUsersPage(Model model) {
        List<User> usersList = userService.getAllUsers();
        model.addAttribute("usersList",usersList);
        return "users";
    }
    @PreAuthorize("hasAuthority('users:write')")
    @GetMapping("/user/{userNum}/{userId}")
    public String showUserPage(@PathVariable int userNum, @PathVariable Long userId, Model model) {
        Optional<User> user = userService.getUser(userId);

        SafeOutputUser sUser = makeSafeUser(user.get(),userNum);
        model.addAttribute("oneUser", sUser);
        return "user";
    }
    @PreAuthorize("hasAuthority('users:write')")
    @GetMapping("/add")
    public String addUser() {
        return "addForm";
    }
    private SafeOutputUser makeSafeUser(User user,int num){
        return new SafeOutputUser(num, user.getId(), user.getUsername(), user.getEmail(), user.getInfo(), user.getRole(), user.getStatus());
    }
}
