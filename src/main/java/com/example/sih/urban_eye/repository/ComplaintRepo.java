package com.example.sih.urban_eye.repository;

import com.example.sih.urban_eye.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author HP
 **/
@Repository
public interface ComplaintRepo extends JpaRepository<Complaint,Integer> {
}
