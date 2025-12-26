package com.example.sih.urban_eye.service;

import com.example.sih.urban_eye.model.Complaint;
import com.example.sih.urban_eye.repository.ComplaintRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepo repo;

    @Autowired
    private GeminiService geminiService;

    // Create complaint but DO NOT save yet (username will be set in controller)
    public Complaint addComplaint(String title, String description, float latitude, float longitude, MultipartFile imageFile) throws IOException {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);
        complaint.setImageName(imageFile.getOriginalFilename());
        complaint.setImageType(imageFile.getContentType());
        complaint.setImageData(imageFile.getBytes());

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
        return repo.findByUsername(username);
    }

    public Complaint getComplaint(int id) {
        Optional<Complaint> comp = repo.findById(id);
        return comp.orElse(null);
    }
}
