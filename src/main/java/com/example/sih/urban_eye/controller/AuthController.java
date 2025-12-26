package com.example.sih.urban_eye.controller;

import org.springframework.http.ResponseEntity;

import com.example.sih.urban_eye.model.User;
import com.example.sih.urban_eye.security.JwtUtil;
import com.example.sih.urban_eye.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;


 
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

    @PostMapping("/signup")
    public String register(@RequestBody User user) {
        userService.saveUser(user);
        return "Signup successful";
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
