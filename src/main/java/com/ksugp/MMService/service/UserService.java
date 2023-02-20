package com.ksugp.MMService.service;

import com.ksugp.MMService.entity.Role;
import com.ksugp.MMService.entity.SafeUser;
import com.ksugp.MMService.entity.Status;
import com.ksugp.MMService.entity.User;
import com.ksugp.MMService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    //@Autowired
    //private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository/*, BCryptPasswordEncoder encoder*/) {
        this.userRepository = userRepository;
        //this.encoder = encoder;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void saveUser(SafeUser sUser) throws RuntimeException {//для сохранения SafeUser
        User user = makeUser(sUser, true);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUser(User user, Long id) {
        user.setId(id);
        userRepository.saveAndFlush(user);
    }

    public List<SafeUser> getSafeUsersList() {
        List<User> usersList = getAllUsers();
        int num = 1;
        List<SafeUser> sUsers = new ArrayList<>();
        for (User user : usersList) {
            sUsers.add(makeSafeUser(user, num));
            num++;
        }
        return sUsers;
    }

    public SafeUser getSafeUser(Long userId, int userNum) {
        Optional<User> user = getUser(userId);
        return makeSafeUser(user.get(), userNum);
    }

    public void createUser(SafeUser safeUser) {
        if (safeUser.getUsername() == null || safeUser.getUsername().equals("")) safeUser.setUsername("default_value");
        if (safeUser.getEmail() == null || safeUser.getEmail().equals("")) safeUser.setEmail("default@email");
        if (safeUser.getInfo() == null || safeUser.getInfo().equals("")) safeUser.setInfo("-");
        if (safeUser.getStatus() != Status.ACTIVE && safeUser.getStatus() != Status.BANNED)
            safeUser.setStatus(Status.BANNED);
        User user = makeUser(safeUser, false);
        updateUser(user, safeUser.getId());
    }

    public void changeUserRights(Long id, Role role) {
        User user = getUser(id).get();
        user.setRole(role);
        updateUser(user, id);
    }

    public void changeMyInformation(SafeUser sUser) {
        User user = getUser(sUser.getId()).get();
        if (!(sUser.getUsername() == null || sUser.getUsername().equals(""))) user.setUsername(sUser.getUsername());
        user.setInfo(sUser.getInfo());
        if (sUser.getInfo() == null || sUser.getInfo().equals("")) user.setInfo("-");
        updateUser(user, user.getId());
    }

    public boolean changeMyPassword(User currentUser) {
        User user = getUser(currentUser.getId()).get();
        if(!confirmPassword(currentUser.getId(), currentUser.getConfirmCheckPassword())) return false;
        if (currentUser.getPassword().length() < 4) return false;
        if (!currentUser.getPassword().equals(currentUser.getPasswordConfirm())) return false;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(currentUser.getPassword()));
        saveUser(user);
        return true;
    }
    public boolean confirmPassword(Long id, String password){
        User user = getUser(id).get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.matches(password, user.getPassword());
    }

    public boolean signupUser(User user) {
        System.out.println(user);
        user.setId(null);
        if (user.getUsername() == null || user.getUsername().equals("")) return false;
        if (user.getEmail() == null || user.getEmail().equals("")) return false;
        if (user.getPassword().length() < 4) return false;
        if (user.getRole() != null) return false;
        if (user.getStatus() != null) return false;
        if (!user.getPassword().equals(user.getPasswordConfirm())) return false;
        if (user.getInfo() == null || user.getInfo().equals("")) user.setInfo("-");
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(user.getPassword()));
        saveUser(user);
        return true;
    }

    private SafeUser makeSafeUser(User user, int num) {
        return new SafeUser(num, user.getId(), user.getUsername(), user.getEmail(), user.getInfo(), user.getRole(), user.getStatus());
    }

    private User makeUser(SafeUser safeUser, boolean passwordRequired) {
        if (passwordRequired) {
            String password = "$2a$12$GMG5084Rb/CW/TFL9EVA9O.gXJryd/h/bcxfZZrDdS4D482jeLBdW";
            return new User(safeUser.getUsername(), password, safeUser.getEmail(), safeUser.getInfo(), /*можно внедрить значение admin*/safeUser.getRole(), safeUser.getStatus());
        } else {
            User userBeforeChange = getUser(safeUser.getId()).get();
            return new User(safeUser.getUsername(), userBeforeChange.getPassword(), safeUser.getEmail(), safeUser.getInfo(), userBeforeChange.getRole(), safeUser.getStatus());
        }
    }
}
