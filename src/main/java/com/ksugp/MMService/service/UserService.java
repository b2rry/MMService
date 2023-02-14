package com.ksugp.MMService.service;

import com.ksugp.MMService.entity.Role;
import com.ksugp.MMService.entity.SafeUser;
import com.ksugp.MMService.entity.Status;
import com.ksugp.MMService.entity.User;
import com.ksugp.MMService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User user){
        userRepository.save(user);
    }
    public void saveUser(SafeUser sUser){//для сохранения SafeUser
        User user = makeUser(sUser, true);
        userRepository.save(user);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
    public void updateUser(User user, Long id){
        user.setId(id);
        userRepository.saveAndFlush(user);
    }
    public List<SafeUser> getSafeUsersList(){
        List<User> usersList = getAllUsers();
        int num = 1;
        List<SafeUser> sUsers = new ArrayList<>();
        for(User user : usersList){
            sUsers.add(makeSafeUser(user, num));
            num++;
        }
        return sUsers;
    }
    public SafeUser getSafeUser(Long userId, int userNum){
        Optional<User> user = getUser(userId);
        return makeSafeUser(user.get(),userNum);
    }
    public void createUser(SafeUser safeUser) {
        if(safeUser.getUsername() == null || safeUser.getUsername().equals("")) safeUser.setUsername("default_value");
        if(safeUser.getEmail() == null || safeUser.getEmail().equals("")) safeUser.setEmail("default@email");
        if(safeUser.getInfo() == null || safeUser.getInfo().equals("")) safeUser.setInfo("-");
        if(safeUser.getRole() != Role.USER && safeUser.getRole() != Role.ADMIN){
            safeUser.setRole(Role.USER);
            safeUser.setStatus(Status.BANNED);
        }
        if(safeUser.getStatus() != Status.ACTIVE && safeUser.getStatus() != Status.BANNED) safeUser.setStatus(Status.BANNED);
        User user = makeUser(safeUser, false);
        updateUser(user,safeUser.getId());
    }
    private SafeUser makeSafeUser(User user, int num){
        return new SafeUser(num, user.getId(), user.getUsername(), user.getEmail(), user.getInfo(), user.getRole(), user.getStatus());
    }
    private User makeUser(SafeUser safeUser, boolean passwordRequired){
        if(passwordRequired){
            String password = "$2a$12$GMG5084Rb/CW/TFL9EVA9O.gXJryd/h/bcxfZZrDdS4D482jeLBdW";
            return new User(safeUser.getUsername(),password,safeUser.getEmail(),safeUser.getInfo(), safeUser.getRole(), safeUser.getStatus());
        }else{
            return new User(safeUser.getUsername(), safeUser.getEmail(),safeUser.getInfo(), safeUser.getRole(), safeUser.getStatus());//bp-pf nfrjuj rjycnhernjhf vj;yj cltkfnm ct,z flvbyjv jnltkmysv ЗЩЫЕ pfghjcjv=)
        }
    }
}
