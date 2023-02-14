package com.ksugp.MMService.controller;

import com.ksugp.MMService.entity.SafeUser;
import com.ksugp.MMService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String showServicePage(Model model) {
        List<SafeUser> sUsers = userService.getSafeUsersList();
        model.addAttribute("usersList",sUsers);
        return "service";
    }
    @PreAuthorize("hasAuthority('users:read')")
    @GetMapping("/user/{userNum}/{userId}")
    public String showUserPage(@PathVariable int userNum, @PathVariable Long userId, Model model) {
        SafeUser sUser = userService.getSafeUser(userId, userNum);
        model.addAttribute("oneUser", sUser);
        return "user";
    }
    @PreAuthorize("hasAuthority('users:write')")
    @GetMapping("/add")
    public String showAddUserForm() {
        return "addForm";
    }
    @PreAuthorize("hasAuthority('users:write')")
    @PostMapping("/add")
    public String takeUserForm(@ModelAttribute("user") SafeUser safeUser){
        userService.saveUser(safeUser);//перегруженный метод
        return "successAdd";
    }
    @PreAuthorize("hasAuthority('users:write')")
    @GetMapping("/change/{userId}")
    public String showChangeUserForm(@PathVariable Long userId, Model model) {
        model.addAttribute("user",userService.getSafeUser(userId,0));
        return "changeUserForm";
    }
    @PreAuthorize("hasAuthority('users:write')")
    @PostMapping("/change/{userId}")
    public String ChangeUser(@ModelAttribute("user") SafeUser safeUser,@PathVariable Long userId){
        safeUser.setId(userId);
        System.out.println(safeUser);
        userService.createUser(safeUser);
        return "successChange";
    }
    @PreAuthorize("hasAuthority('users:write')")
    @GetMapping("/delete/{userId}")
    public String showDeleteUserForm(@PathVariable Long userId, Model model) {
        model.addAttribute("userId",userId);
        return "deleteUserForm";
    }
    @PreAuthorize("hasAuthority('users:write')")
    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "successDelete";
    }
}
