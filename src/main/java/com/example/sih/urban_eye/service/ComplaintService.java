package com.example.sih.urban_eye.service;

import com.example.sih.urban_eye.model.Complaint;
import com.example.sih.urban_eye.repository.ComplaintRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ComplaintService {
    @Autowired
    ComplaintRepo repo;

    @Autowired
    GeminiService geminiService;

    @Value("${api.key}")
    private String apiKey;

    public List<Complaint> getAllComplaints() {
        return repo.findAll();
    }

    public Complaint getComplaint(int id) {
        return repo.findById(id).orElse(new Complaint());
    }

    public Complaint addComplaint(String title, String description, float latitude, float longitude, String imageUri) throws IOException {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);
        complaint.setImageUri(imageUri);
        // complaint.setImageName(imageFile.getOriginalFilename());
        // complaint.setImageType(imageFile.getContentType());
        // complaint.setImageData(imageFile.getBytes());

        try {
            JSONObject aiResult = geminiService.analyzeComplaint(description);
            complaint.setCategory(aiResult.optString("category", "Miscellaneous"));
            complaint.setPriority(aiResult.optString("priority", "Medium"));
        } catch (Exception e) {
            e.printStackTrace();
            complaint.setCategory("Uncategorized");
            complaint.setPriority("Medium");
        }

        return repo.save(complaint);
    }


}
