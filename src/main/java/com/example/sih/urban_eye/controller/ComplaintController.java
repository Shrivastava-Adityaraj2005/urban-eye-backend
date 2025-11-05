package com.example.sih.urban_eye.controller;

import com.example.sih.urban_eye.model.Complaint;
import com.example.sih.urban_eye.service.ComplaintService;
import com.example.sih.urban_eye.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HP
 **/
@RestController
//@RequestMapping("/")
@CrossOrigin
public class ComplaintController {
    @Autowired
    ComplaintService service;
    @Autowired
    S3Service s3Service;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("appName", "Urban Eye");
        info.put("description", "Urban Eye is a smart city complaint management system that uses AI (Gemini) to categorize and prioritize civic issues based on user-submitted reports.");
        info.put("availableEndpoints", List.of(
                "GET /complaints - Fetch all complaints",
                "GET /complaint/{id} - Fetch a specific complaint by ID",
                "POST /complaint - Submit a new complaint (with image)"
        ));
        info.put("version", "1.0.0");
        info.put("status", "Running");

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints(){
        return new ResponseEntity<>(service.getAllComplaints(), HttpStatus.OK);

    }
    @GetMapping("/complaint/{id}")
    public ResponseEntity<Complaint> getComplaint(@PathVariable int id){
        Complaint complaint = service.getComplaint(id);
        if(complaint!=null){
            return new ResponseEntity<>(complaint, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/complaint")
    public ResponseEntity<Map<String, Object>> addComplaint(@RequestParam String title, @RequestParam String description,
                                            @RequestParam float latitude, @RequestParam float longitude
                                       , @RequestPart MultipartFile imageFile) throws IOException {
        System.out.print(title);
        try{
            // here first send description to gemini and get priority and category then it will be added

            String imageUri = s3Service.uploadFile(imageFile);
            Complaint complaint = service.addComplaint(title,description,latitude,longitude,imageUri);

            Map<String, Object> response = new HashMap<>();
            response.put("id", complaint.getId());
            response.put("category", complaint.getCategory());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){
//            e.printStackTrace();
            System.out.println( "Upload failed: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
