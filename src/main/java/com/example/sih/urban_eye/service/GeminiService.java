package com.example.sih.urban_eye.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
public class GeminiService {
    @Value("${api.key}")
    private String apiKey;


    public JSONObject analyzeComplaint(String description) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        String prompt = "You are given a citizen complaint. Categorize it (Sanitation & Waste Management, Roads & Transportation, " +
                "Water Supply & Drainage, Electricity & Street Lighting, Public Safety & Law Enforcement, Health & Environment, Parks & Public Spaces, " +
                "Building & Infrastructure, Transport & Mobility, Citizen Services, Miscellaneous) " +
                "and assign a priority (High, Medium, Low). " +
                "Return JSON only in format: {\"category\": \"<category>\", \"priority\": \"<priority>\"}. " +
                "Complaint: " + description;

        System.out.println(apiKey);
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println(response.getBody());

        // Parse Gemini’s outer response
        JsonNode root = mapper.readTree(response.getBody());
        String outputText = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
        String cleaned = outputText
                .replace("```json", "")
                .replace("```", "")
                .trim();
        // System.out.print(cleaned);
        // Now parse the JSON text that Gemini returned
         return new JSONObject(cleaned);
    }
}

