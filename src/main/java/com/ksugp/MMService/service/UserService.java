package com.ksugp.MMService.service;

import com.ksugp.MMService.entity.User;
import com.ksugp.MMService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
