package com.example.sih.urban_eye.repository;
import java.util.*;
import com.example.sih.urban_eye.model.User;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepo extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);

}
