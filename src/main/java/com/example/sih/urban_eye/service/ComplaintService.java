package com.example.sih.urban_eye.service;

import com.example.sih.urban_eye.model.Complaint;
import com.example.sih.urban_eye.repository.ComplaintRepo;
import com.example.sih.urban_eye.repository.TaskRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepo repo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private GeminiService geminiService;

    // Create complaint but DO NOT save yet (username will be set in controller)
    public Complaint addComplaint(String title, String description, float latitude, float longitude, String imageUri, String publicId) throws IOException {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);
        complaint.setImageUri(imageUri);
        complaint.setPublicId(publicId);

        try {
            JSONObject aiResult = geminiService.analyzeComplaint(description);
            complaint.setCategory(aiResult.optString("category", "Miscellaneous"));
            complaint.setPriority(aiResult.optString("priority", "Medium"));
        } catch (Exception e) {
            complaint.setCategory("Uncategorized");
            complaint.setPriority("Medium");
        }

        return complaint; // Do NOT save here
    }

    public Complaint saveComplaint(Complaint complaint) {
        return repo.save(complaint);
    }

    public List<Complaint> getMyComplaints(String username) {
        if ("admin".equalsIgnoreCase(username)) {
            return repo.findAll();
        }
        return repo.findByUsername(username);
    }
    public Complaint getComplaint(int id) {
        Optional<Complaint> comp = repo.findById(id);
        return comp.orElse(null);
    }

    // worker work
    public void setAssign(int id){
        Complaint comp = repo.findById(id).orElse(null);
        comp.setAssigned(true);
        repo.save(comp);
    }
    public List<Complaint> getNewComplaints(){
        return repo.findByStatus("In Queue");
    
    }
    public List<Complaint> getOldComplaints(String workername) {
        return repo.findByWorkername(workername);
    }
    public void updateWorkerDetails(int id, String workername) {
        
        Complaint complaint = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        
        complaint.setWorkerName(workername);
        complaint.setStatus("In Progress");
        repo.save(complaint);
    }
    public void updateStatus(int id, String status) {
        Complaint complaint = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(status);
        repo.save(complaint);
    }
    public void updateName(int id, String workername){
    Complaint complaint = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

            complaint.setWorkerName(workername);
            repo.save(complaint);
    }
    public void updateFinalImageUri(int id, String imageUri){
        Complaint complaint = repo.findById(id).orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setFinalImageUri(imageUri);
        repo.save(complaint);
    }
    public void updateFinalPublicId(int id, String publicId) {
        Complaint complaint = repo.findById(id).orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setFinalPublicId(publicId);
        repo.save(complaint);
    }
}