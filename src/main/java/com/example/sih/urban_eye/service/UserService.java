package com.example.sih.urban_eye.service;

import java.util.*;
import com.example.sih.urban_eye.model.User;
import com.example.sih.urban_eye.repository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder encoder;

    public User saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public User getUser(String username) {
    List<User> users = userRepo.findByUsername(username); // <-- changed repo → userRepo

    if (users == null || users.isEmpty()) {
        return null;
    }

    return users.get(0);
}


}
