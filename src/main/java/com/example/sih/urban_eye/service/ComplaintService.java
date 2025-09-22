package com.example.sih.urban_eye.service;

import com.example.sih.urban_eye.model.Complaint;
import com.example.sih.urban_eye.repository.ComplaintRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author HP
 **/
@Service
public class ComplaintService {
    @Autowired
    ComplaintRepo repo;



    public List<Complaint> getAllComplaints(){

        return repo.findAll();
    }

    public Complaint addComplaint(String title, String description, float latitude, float longitude, MultipartFile imageFile) throws IOException {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setLatitude(latitude);
        complaint.setLongitude(longitude);
        complaint.setImageName(imageFile.getOriginalFilename());
        complaint.setImageType(imageFile.getContentType());
        complaint.setImageData(imageFile.getBytes());
        return repo.save(complaint);
    }

    public Complaint getComplaint(int id) {
        return repo.findById(id).orElse(new Complaint());
    }
}
