package com.example.sih.urban_eye.config;
import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud.name}")
    private String cloudinaryCloudName;

    @Value("${cloudinary.api.key}")
    private String cloudinaryApikey;

    @Value("${cloudinary.api.secret}")
    private String cloudinaryApiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudinaryCloudName);
        config.put("api_key", cloudinaryApikey);
        config.put("api_secret", cloudinaryApiSecret);

        return new Cloudinary(config);
    }
}