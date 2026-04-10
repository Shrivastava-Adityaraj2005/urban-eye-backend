package com.example.sih.urban_eye.controller;

import com.cloudinary.Cloudinary;
import jakarta.servlet.http.HttpServletRequest; 
import com.example.sih.urban_eye.model.Complaint;
import com.example.sih.urban_eye.security.JwtUtil; 
import com.example.sih.urban_eye.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin
public class ComplaintController {

    @Autowired
    ComplaintService service;

    @Autowired
    private JwtUtil jwtUtil; 

    @Autowired
    private Cloudinary cloudinary;

    @Value("${cloudinary.cloud.name}")
    private String cloudinaryCloudName;

    @Value("${cloudinary.api.key}")
    private String cloudinaryApikey;

    @Value("${cloudinary.api.secret}")
    private String cloudinaryApiSecret;

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
    // ⭐ UPDATED to return only logged-in user's complaints
    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token); // ⭐ ADDED

        return new ResponseEntity<>(service.getMyComplaints(username), HttpStatus.OK); // ⭐ ADDED
    }

    @GetMapping("/complaint/{id}")
    public ResponseEntity<Complaint> getComplaint(@PathVariable int id, HttpServletRequest request) { // ⭐ ADDED request

        Complaint complaint = service.getComplaint(id);
        if (complaint == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (!username.equalsIgnoreCase("admin")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(complaint, HttpStatus.OK);
    }

    @PostMapping("/complaint")
    public ResponseEntity<?> addComplaint(
            HttpServletRequest request,
            @RequestBody Map<String, Object> body) {

        String title = (String) body.get("title");
        String description = (String) body.get("description");
        float latitude = Float.parseFloat(body.get("latitude").toString());
        float longitude = Float.parseFloat(body.get("longitude").toString());
        String imageUri = (String) body.get("imageUri");
        String publicId = (String) body.get("publicId");

        System.out.print(title);
        try {
            Complaint complaint = service.addComplaint(title, description, latitude, longitude, imageUri, publicId);

            // ⭐ Extract Username from Token & Save
            String token = request.getHeader("Authorization").substring(7);
            String username = jwtUtil.extractUsername(token);
            complaint.setUsername(username);
            service.saveComplaint(complaint);

            Map<String, Object> response = new HashMap<>();
            response.put("id", complaint.getId());
            response.put("category", complaint.getCategory());
            response.put("status", complaint.getStatus());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.println("Upload failed: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/complaint/assign-task/{id}")
    public ResponseEntity<?> assignTask(@PathVariable int id){
        System.out.println(id);
        service.setAssign(id);
        return new ResponseEntity<>("Updated",HttpStatus.OK);
    }
    //worker routes
    @GetMapping("/complaints/new-jobs")
    public ResponseEntity<?> getRadiusComplaint(@RequestParam double lat, @RequestParam double lng) {
        List<Complaint> newComplaints = service.getNewComplaints();
        List<Complaint> nearbyComplaints = new ArrayList<>();
        double radius = 800;
        for(Complaint c: newComplaints){
            double dist = Math.sqrt(Math.pow(c.getLatitude()-lat, 2) + Math.pow(c.getLongitude()-lng, 2));
            if (dist < radius) {
                nearbyComplaints.add(c);
            }      
        }

        return new ResponseEntity<>(nearbyComplaints, HttpStatus.OK);
    }
    @GetMapping("/complaints/old-jobs")
    public ResponseEntity<?> getOldJobs(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        return new ResponseEntity<>(service.getOldComplaints(username), HttpStatus.OK);
    }
    
    @PatchMapping("/complaint/{id}/in-queue")
    public ResponseEntity<?> markInQueue(@PathVariable int id) {
        service.updateStatus(id, "In Queue");
        return new ResponseEntity<>("Marked as In Queue", HttpStatus.OK);
    }

    @PatchMapping("/complaint/grab-job")
    public ResponseEntity<?> markInProgress(@RequestBody Map<String, Object> body) {

        Integer id = (Integer) body.get("complaintId");
        String workername = (String) body.get("workername");

        if (id == null || workername == null) {
            return ResponseEntity.badRequest().body("Missing fields");
        }

        service.updateStatus(id, "In Progress");
        service.updateName(id, workername);

        return ResponseEntity.ok("Marked as In Progress");
    }
    @PatchMapping("/complaint/submit-work")
    public ResponseEntity<?> markUnderVerification(@RequestBody Map<String, Object> body) {
        Integer complaintId = (Integer) body.get("complaintId");
        String imageUri = (String) body.get("imageUri");
        String publicId = (String) body.get("publicId");

        service.updateStatus(complaintId, "Under review");
        service.updateFinalImageUri(complaintId, imageUri);
        service.updateFinalPublicId(complaintId, publicId);
        return new ResponseEntity<>("Marked as Under Verification", HttpStatus.OK);
    }

    @PatchMapping("/complaint/verify")
    public ResponseEntity<?> markCompleted(@RequestBody Map<String, Object> body) {
        Integer complaintId = (Integer) body.get("complaintId");
        service.updateStatus(complaintId, "Completed");
        return new ResponseEntity<>("Marked as Completed", HttpStatus.OK);
    }

    @GetMapping("cloudinary/generate-signature")
    public ResponseEntity<?> generateSignature() {

        long timestamp = System.currentTimeMillis() / 1000;

        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);
        params.put("folder", "urban-eye");

        String signature = cloudinary.apiSignRequest(params, cloudinaryApiSecret);

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", timestamp);
        response.put("signature", signature);
        response.put("apiKey", cloudinaryApikey);
        response.put("cloudName", cloudinaryCloudName);
        response.put("folder", "urban-eye");
        return ResponseEntity.ok(response);
    }
}