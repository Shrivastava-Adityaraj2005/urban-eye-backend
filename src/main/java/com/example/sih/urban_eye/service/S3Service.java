package com.example.sih.urban_eye.service;

import com.example.sih.urban_eye.repository.ComplaintRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
// import java.net.URL;
// import java.time.Instant;
import java.util.UUID;

@Service
public class S3Service {
    @Autowired
    ComplaintRepo repo;
    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretAccessKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        // create S3 client
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();

        // generate a unique file name
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // upload to S3
        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        // return public URL (if your bucket allows public read)
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }
}
