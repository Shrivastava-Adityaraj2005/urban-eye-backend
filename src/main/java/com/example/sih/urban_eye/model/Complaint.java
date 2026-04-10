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
    @Column(nullable = false)
    private String username;

    private float latitude;
    private float longitude;
    private String title;
    private String description;
    private String priority;
    private String category;

    private String imageUri;
    private String publicId;
    private String finalImageUri;
    private String finalPublicId;
    private String workername;
    private boolean assigned;
    private String status = "Pending";
    
    // private String imageName;
    // private String imageType;
    // @Lob
    // private byte[] imageData;
    // @Transient
    // public String getImageBase64() {
    //     return imageData != null ? Base64.getEncoder().encodeToString(imageData) : null;
    // }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public String getFinalImageUri() {
        return finalImageUri;
    }

    public void setFinalImageUri(String finalImageUri) {
        this.finalImageUri = finalImageUri;
    }
    public String getPublicId(){
        return publicId;
    }
    public void setPublicId(String publicId){
        this.publicId=publicId;
    }
    // public String getImageName() {
    //     return imageName;
    // }

    // public void setImageName(String imageName) {
    //     this.imageName = imageName;
    // }

    // public String getImageType() {
    //     return imageType;
    // }

    // public void setImageType(String imageType) {
    //     this.imageType = imageType;
    // }

    // public byte[] getImageData() {
    //     return imageData;
    // }

    // public void setImageData(byte[] imageData) {
    //     this.imageData = imageData;
    // }

    public void setWorkerName(String workername){
        this.workername=workername;

    }
    public String getWorkerName(){
        return workername;
    }
    public void setAssigned(boolean assigned){
        this.assigned = assigned;
    }
    public boolean getAssigned(){
        return assigned;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return status;
    }
}
