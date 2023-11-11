package com.medilabo.front.controller;

import com.medilabo.front.model.PatientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static com.medilabo.front.Constants.URL_GATEWAY;

/**
 * Send requests to Patients Back
 */
@Controller
public class FrontPatientController {

    private static Logger logger = LoggerFactory.getLogger(FrontPatientController.class);

    private final RestTemplate restTemplate;


    /**
     * Add the basic auth header to the outgoing requests
     * Request the gateway to get the base64 authentication header of the connected user
     *
     * @param restTemplate request to the back
     */
    public FrontPatientController(RestTemplate restTemplate) {
        logger.debug("Setting FrontPatientController constructor");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(("user:user").getBytes()) );
        logger.info(headers.toString());
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> finalHeader = restTemplate.exchange(URL_GATEWAY+"/auth/header", HttpMethod.GET, entity, String.class);
        ClientHttpRequestInterceptor interceptor = (httpRequest, bytes, execution) -> {
            httpRequest.getHeaders().set("Authorization", finalHeader.getBody());
            return execution.execute(httpRequest, bytes);
        };

        restTemplate.getInterceptors().add(interceptor);
        logger.debug("Interceptor set for headers of outgoing requests");
        this.restTemplate = restTemplate;
    }


    /**
     * Request the list of all patients to the back patient
     * @param model to collect list of all patients
     * @return page patients with all patients
     */
    @GetMapping("/patients")
    public String getPatients(Model model)  {
        logger.debug("get patients");
        ResponseEntity<PatientDTO[]> response = restTemplate.exchange(URL_GATEWAY+"/patients", HttpMethod.GET, null, PatientDTO[].class);
        List<PatientDTO> patients = Arrays.asList(response.getBody());
        model.addAttribute("listPatients", patients);
        return "patients";
    }

    /**
     * Display the form to create a new patient
     * @param model new patient
     * @return page addPatient
     */
    @GetMapping("/patients/new")
    public String getAddPatient(Model model) {
        logger.debug("get form new patient");
        model.addAttribute("patient", new PatientDTO());

        return "addPatient";
    }

    /**
     * Save the form as a new patient
     *
     * @param patient to add to database
     * @return page patients with the new patient
     */
    @PostMapping("/patients")
    public String addPatient(PatientDTO patient) {
        logger.info("saving new patient : "+patient.getNom()+" "+patient.getPrenom());
        restTemplate.postForEntity(URL_GATEWAY+"/patients", patient, PatientDTO.class);
        return "redirect:/patients";
    }

    /**
     * Display the update patient form
     *
     * @param id of the patient to update
     * @param model to update
     * @return the update patient form
     */
    @GetMapping("/patients/{id}")
    public String getUpdatePatient(@PathVariable Integer id, Model model) {
        ResponseEntity<PatientDTO> response = restTemplate.exchange(URL_GATEWAY+"/patients/"+id, HttpMethod.GET, null, PatientDTO.class);
        PatientDTO patient = response.getBody();
        model.addAttribute("patient", patient);
        return "updatePatient";
    }

    /**
     *send the update request to the gateway
     *
     * @param patient to update
     * @return list of patients
     */
    @PostMapping("/patients/{id}/update")
    public String updatePatient(@PathVariable Integer id, PatientDTO patient) {
        logger.info("updating patient n°"+id);
        restTemplate.postForEntity(URL_GATEWAY+"/patients/"+id+"/update", patient, PatientDTO.class);
        return "redirect:/patients";
    }

    /**
     * Display the form with the patient to delete
     * @param id id of the patient to delete
     * @param model patient to delete
     * @return delete patient form
     */
    @GetMapping("/patients/{id}/delete")
    public String getDeletePatient(@PathVariable Integer id, Model model) {
        logger.debug("get delete patient form");
        ResponseEntity<PatientDTO> response = restTemplate.exchange(URL_GATEWAY+"/patients/"+id, HttpMethod.GET, null, PatientDTO.class);
        PatientDTO patient = response.getBody();
        model.addAttribute("patient", patient);
        return "deletePatient";
    }


    /**
     * Delete the patient corresponding to the id
     * @param id of the patient to delete
     * @return the page patients without the deleted patient
     */
    @PostMapping("/patients/{id}/delete")
    public String deletePatient(@PathVariable(value = "id") Integer id) {
        logger.info("Deleting patient n°"+id);
        restTemplate.exchange(URL_GATEWAY+"/patients/"+ id+"/delete", HttpMethod.POST, null, Void.class);
        return "redirect:/patients";

    }


}
