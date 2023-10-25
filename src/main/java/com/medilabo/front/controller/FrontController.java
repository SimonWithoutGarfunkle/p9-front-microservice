package com.medilabo.front.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Controller
public class FrontController {

    private final RestTemplate restTemplate;

    public FrontController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/patients")
    public String getPatients(Model model) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9001/api/patients", HttpMethod.GET, null, String.class);
        String patientsJson = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> patients = objectMapper.readValue(patientsJson, new TypeReference<List<Map<String, Object>>>() {});

        model.addAttribute("listPatients", patients);

        return "patients";
    }
}
