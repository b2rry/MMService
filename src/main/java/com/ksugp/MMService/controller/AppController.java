package com.ksugp.MMService.controller;

import com.ksugp.MMService.model.SafeUser;
import com.ksugp.MMService.model.User;
import com.ksugp.MMService.service.AuthService;
import com.ksugp.MMService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/service")
public class AppController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final AuthService authService;

    public AppController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PreAuthorize("hasAuthority('users:read')")
    @GetMapping
    public String showServicePage(Model model, HttpServletRequest request) {
        List<SafeUser> sUsers = userService.getSafeUsersList();
        SafeUser currentSUser = authService.getJwtTokenProvider().getFullClaimsInfoInSafeUserClass(authService.getJwtTokenProvider().resolveToken(request));
        model.addAttribute("currentSUser", currentSUser);
        model.addAttribute("usersList",sUsers);
        return "service";
    }
    @PreAuthorize("hasAuthority('users:read')")
    @GetMapping("/user/{userNum}/{userId}/{myEmail}")
    public String showUserPage(@PathVariable int userNum, @PathVariable Long userId, Model model, @PathVariable String myEmail) {
        SafeUser sUser = userService.getSafeUser(userId, userNum);
        model.addAttribute("oneUser", sUser);
        model.addAttribute("myEmail", myEmail);
        return "user";
    }

    //------------------------------------------

    @PreAuthorize("hasAuthority('users:write')")
    @GetMapping("/user/add")
    public String showAddUserForm() {
        return "addUserForm";
    }

    @PreAuthorize("hasAuthority('users:write')")
    @PostMapping("/user/add")
    public String takeUserForm(@Valid @ModelAttribute("user") SafeUser safeUser) {
        userService.saveUser(safeUser);//перегруженный метод
        return "successAdd";
    }

    //------------------------------------------

    @PreAuthorize("hasAuthority('users:write')")
    @GetMapping("/user/change/{userId}/{myEmail}")
    public String showChangeUserForm(@PathVariable Long userId, Model model, @PathVariable String myEmail) {
        SafeUser sUser = userService.getSafeUser(userId,0);
        if(sUser.getEmail().equals(myEmail)){
            return "noChangeMyselfError";
        }else {
            model.addAttribute("user", sUser);
            return "changeUserForm";
        }
    }
    @PreAuthorize("hasAuthority('users:write')")
    @PostMapping("/user/change/{userId}")
    public String ChangeUser(@ModelAttribute("user") SafeUser safeUser,@PathVariable Long userId){
        safeUser.setId(userId);
        userService.createUser(safeUser);
        return "successChange";
    }

    //------------------------------------------

    @PreAuthorize("hasAuthority('users:read')")
    @GetMapping("/user/change/my/information")
    public String showChangeMyUserInformationForm(Model model, HttpServletRequest request) {
        SafeUser currentSUser = authService.getJwtTokenProvider().getFullClaimsInfoInSafeUserClass(authService.getJwtTokenProvider().resolveToken(request));
        model.addAttribute("currentSUser",userService.getSafeUser(currentSUser.getId(),0));
        return "changeMyInformationForm";
    }
    @PreAuthorize("hasAuthority('users:read')")
    @PostMapping("/user/change/my/information")
    public String changeMyUserInformation(@ModelAttribute("user") SafeUser currentSUser, HttpServletRequest request) {
        SafeUser currentSUserFromToken = authService.getJwtTokenProvider().getFullClaimsInfoInSafeUserClass(authService.getJwtTokenProvider().resolveToken(request));
        currentSUser.setId(currentSUserFromToken.getId());
        userService.changeMyInformation(currentSUser);
        return "successChange";
    }


    //------------------------------------------

    @PreAuthorize("hasAuthority('users:read')")
    @GetMapping("/user/change/my/password")
    public String showChangeMyUserPasswordForm(Model model, HttpServletRequest request) {
        SafeUser currentSUser = authService.getJwtTokenProvider().getFullClaimsInfoInSafeUserClass(authService.getJwtTokenProvider().resolveToken(request));
        model.addAttribute("currentSUser",userService.getSafeUser(currentSUser.getId(),0));
        return "changeMyPasswordForm";
    }
    @PreAuthorize("hasAuthority('users:read')")
    @PostMapping("/user/change/my/password")
    public String changeMyUserPassword(@ModelAttribute("currentUser") User currentUser, HttpServletRequest request, HttpServletResponse response) {
        SafeUser currentSUserFromToken = authService.getJwtTokenProvider().getFullClaimsInfoInSafeUserClass(authService.getJwtTokenProvider().resolveToken(request));
        currentUser.setId(currentSUserFromToken.getId());
        if(userService.changeMyPassword(currentUser)){
            authService.deleteAuthCookie(response);
            return "successPasswordChange";
        }else{
            return "errorPasswordChange";//change
        }
    }

    //------------------------------------------

    @PreAuthorize("hasAuthority('users:writeplus')")
    @GetMapping("/user/change/rights/{userId}")
    public String showChangeUserRightsForm(@PathVariable Long userId, Model model) {
        model.addAttribute("user",userService.getSafeUser(userId,0));
        return "changeUserRightsForm";
    }
    @PreAuthorize("hasAuthority('users:writeplus')")
    @PostMapping("/user/change/rights/{userId}")
    public String ChangeUserRights(@ModelAttribute("user") User user,@PathVariable Long userId, HttpServletRequest request){
        SafeUser currentSUserFromToken = authService.getJwtTokenProvider().getFullClaimsInfoInSafeUserClass(authService.getJwtTokenProvider().resolveToken(request));
        if(userService.confirmPassword(currentSUserFromToken.getId(), user.getConfirmCheckPassword())){
            userService.changeUserRights(userId, user.getRole());
            return "successChange";
        }
        return "confirmError";
    }

    //------------------------------------------

    @PreAuthorize("hasAuthority('users:writeplus')")
    @GetMapping("/user/delete/{userId}/{myEmail}")
    public String showDeleteUserForm(@PathVariable Long userId, Model model, @PathVariable String myEmail) {
        SafeUser sUser = userService.getSafeUser(userId,0);
        if(sUser.getEmail().equals(myEmail)){
            return "noChangeMyselfError";
        }else {
            model.addAttribute("userId",userId);
            return "deleteUserForm";
        }
    }
    @PreAuthorize("hasAuthority('users:writeplus')")
    @PostMapping("/user/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "successDelete";
    }
}
