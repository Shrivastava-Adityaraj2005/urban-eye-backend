package com.example.sih.urban_eye.controller;

import org.springframework.http.ResponseEntity;
import com.example.sih.urban_eye.model.User;
import com.example.sih.urban_eye.security.JwtUtil;
import com.example.sih.urban_eye.service.UserService;
import com.example.sih.urban_eye.repository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

 
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody User user) {
        if(user.getUsername()==null || user.getUsername().strip().length()==0){
            return ResponseEntity.status(400).body("Username can't be empty");
        }
        if(user.getPassword()==null || user.getPassword().strip().length()<4){
            return ResponseEntity.status(400).body("Password length should be >= 4");
        }
        if(user.getRole()==null || !user.getRole().equals("user") && !user.getRole().equals("worker") && !user.getRole().equals("admin")) {
            return ResponseEntity.status(400).body("Role must be either 'user', 'worker' or 'admin'");
        }

        List<User> users = userRepo.findByUsername(user.getUsername());
        if(!users.isEmpty()){
            return ResponseEntity.status(400).body("User already exists");
        }
        userService.saveUser(user);

        return ResponseEntity.status(201).body("Signup successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User dbUser = userService.getUser(user.getUsername());

        if (dbUser == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }




}
