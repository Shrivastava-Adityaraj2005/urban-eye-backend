package com.example.sih.urban_eye.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Base64;

/**
 * @author HP
 **/
@Entity
@Data
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private float latitude;
    private float longitude;
    private String title;
    private String description;
    private String priority;
    private String category;
    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;
    @Transient
    public String getImageBase64() {
        return imageData != null ? Base64.getEncoder().encodeToString(imageData) : null;
    }

}
