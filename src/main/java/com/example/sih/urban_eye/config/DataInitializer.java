package com.example.sih.urban_eye.config;

import com.example.sih.urban_eye.model.User;
import com.example.sih.urban_eye.repository.UserRepo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${admin.password}")
    private String adminPassword;
    
    @Value("${db.connect.cmd}")
    private String dbConnectCmd;

    @Autowired
    private PasswordEncoder encoder;

    @Bean
    CommandLineRunner initAdmin(UserRepo userRepo) {
        return args -> {

            // check if admin already exists
            if (userRepo.findByUsername("admin").isEmpty()) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRole("admin");

                userRepo.save(admin);
                System.out.println("Admin user created");
            } else {
                System.out.println("Admin user already exists");
            }
            System.out.println("Command to connect to the database:\n" + dbConnectCmd);
            System.out.println("\n[Server started at PORT 8080]");
        };
    }
}
