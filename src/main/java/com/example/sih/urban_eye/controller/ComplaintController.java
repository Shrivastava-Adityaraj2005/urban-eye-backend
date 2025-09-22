package com.example.sih.urban_eye.controller;

import com.example.sih.urban_eye.model.Complaint;
import com.example.sih.urban_eye.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author HP
 **/
@RestController
//@RequestMapping("/")
@CrossOrigin
public class ComplaintController {
    @Autowired
    ComplaintService service;

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
    public ResponseEntity<?> addComplaint(@RequestParam String title, @RequestParam String description,
                                       @RequestParam float latitude, @RequestParam float longitude
                                       ,@RequestPart MultipartFile imageFile) throws IOException {
        System.out.print(title);
        try{
            
            Complaint complaint = service.addComplaint(title,description,latitude,longitude,imageFile);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
