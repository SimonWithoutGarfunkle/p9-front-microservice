package com.medilabo.front.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.medilabo.front.Constants.URL_GATEWAY;

@Controller
public class FrontPatientController {

    private static Logger logger = LoggerFactory.getLogger(FrontPatientController.class);

    private final RestTemplate restTemplate;

    public FrontPatientController(RestTemplate restTemplate) {
        logger.info("FrontPatientController constructor");
        ClientHttpRequestInterceptor interceptor = (httpRequest, bytes, execution) -> {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String headerValue = getBasicAuthenticationHeader(user.getUsername(), "user");
            logger.info("username : "+user.getUsername());
            logger.info("user password : "+user.getPassword());
            httpRequest.getHeaders().set("Authorization", headerValue);
            return execution.execute(httpRequest, bytes);
        };

        restTemplate.getInterceptors().add(interceptor);

        this.restTemplate = restTemplate;
    }

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    private String generateHeader() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String header = getBasicAuthenticationHeader(user.getUsername(), user.getPassword());
        logger.info("username : "+user.getUsername());
        logger.info("user password : "+user.getPassword());
        return header;
    }


    @GetMapping("/patients")
    public String getPatients(Model model) throws JsonProcessingException {
        logger.info("get patients");
        ResponseEntity<String> response = restTemplate.exchange(URL_GATEWAY+"/patients", HttpMethod.GET, null, String.class);
        String patientsJson = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> patients = objectMapper.readValue(patientsJson, new TypeReference<List<Map<String, Object>>>() {});

        model.addAttribute("listPatients", patients);

        return "patients";
    }

    @GetMapping("/patients/new")
    public String getAddPatient() {
        return "addPatient";
    }

    @PostMapping("/patients")
    public String addPatient(@RequestParam String prenom, @RequestParam String nom, @RequestParam String genre, @RequestParam String adresse, @RequestParam String telephone) {
        String query ="prenom="+prenom+"&nom="+nom+"&genre="+genre;
        if (!adresse.isEmpty()) {
            query+="&adresse="+adresse;
        }
        if (!telephone.isEmpty()) {
            query+="&telephone="+telephone;
        }
        restTemplate.exchange(URL_GATEWAY+"/patients?"+query, HttpMethod.POST, null, String.class);


        return "patients";

    }



    @DeleteMapping("/patients/{id}")
    public String deletePatient(@PathVariable(value = "id") Integer id) {
        restTemplate.delete(URL_GATEWAY+"/patients/"+ id);

        return "redirect:/patients";

    }


}
