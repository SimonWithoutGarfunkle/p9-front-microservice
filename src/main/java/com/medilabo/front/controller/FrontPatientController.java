package com.medilabo.front.controller;

import com.medilabo.front.model.Patient;
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

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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


    @GetMapping("/patients")
    public String getPatients(Model model)  {
        logger.info("get patients");
        ResponseEntity<Patient[]> response = restTemplate.exchange(URL_GATEWAY+"/patients", HttpMethod.GET, null, Patient[].class);
        List<Patient> patients = Arrays.asList(response.getBody());

        model.addAttribute("listPatients", patients);

        return "patients";
    }

    @GetMapping("/patients/new")
    public String getAddPatient(Model model) {
        model.addAttribute("patient", new Patient());

        return "addPatient";
    }

    @PostMapping("/patients")
    public String addPatient(Patient patient) {


        logger.info("patient :"+patient.getNom()+" "+patient.getPrenom());
        logger.info("patient id : "+patient.getIdPatient());
        restTemplate.postForEntity(URL_GATEWAY+"/patients?", patient, Patient.class);

        return "redirect:/patients";

    }

    @GetMapping("/patients/delete/{idPatient}")
    public String getDeletePatient(@PathVariable Integer idPatient, Model model) {
        ResponseEntity<Patient> response = restTemplate.exchange(URL_GATEWAY+"/patients/"+idPatient, HttpMethod.GET, null, Patient.class);
        Patient patient = response.getBody();
        model.addAttribute("patient", patient);
        return "deletePatient";
    }


    @DeleteMapping("/patients/{id}")
    public String deletePatient(@PathVariable(value = "id") Integer id) {
        restTemplate.delete(URL_GATEWAY+"/patients/"+ id);

        return "redirect:/patients";

    }


}
