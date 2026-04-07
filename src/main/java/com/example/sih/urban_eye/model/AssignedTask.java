package com.example.sih.urban_eye.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Base64;

@Entity
@Data
public class AssignedTask{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int workerId;
    private int complaintId;
    private String status;
    
}