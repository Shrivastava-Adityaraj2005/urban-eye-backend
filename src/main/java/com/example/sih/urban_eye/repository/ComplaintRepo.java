package com.example.sih.urban_eye.repository;

import com.example.sih.urban_eye.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepo extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByUsername(String username);
}
