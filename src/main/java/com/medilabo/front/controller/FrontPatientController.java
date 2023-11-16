package com.medilabo.front.controller;

import com.medilabo.front.model.FichePatientDTO;
import com.medilabo.front.model.NoteDTO;
import com.medilabo.front.model.PatientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.medilabo.front.Constants.URL_GATEWAY;

/**
 * Send requests to Patients Back
 */
@Controller
public class FrontPatientController {

    private static Logger logger = LoggerFactory.getLogger(FrontPatientController.class);

    private final RestTemplate restTemplate;

    @Autowired
    public FrontPatientController(RestTemplate restTemplate) {
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
     * This page is divided into 2 parts : the patient from and his history
     *
     * @param id of the patient to update
     * @param model to update
     * @return the update patient form
     */
    @GetMapping("/patients/{id}")
    public String getUpdatePatient(@PathVariable Integer id, Model model) {
        // Get the info from the patient
        ResponseEntity<PatientDTO> response = restTemplate.exchange(URL_GATEWAY+"/patients/"+id, HttpMethod.GET, null, PatientDTO.class);
        PatientDTO patient = response.getBody();
        model.addAttribute("patient", patient);

        // Get the history from the patient
        ResponseEntity<FichePatientDTO> fiche = restTemplate.exchange(URL_GATEWAY+"/doctor/"+id, HttpMethod.GET, null, FichePatientDTO.class);
        // If the patient is new and doest have a fiche, create it and save it in database
        if (fiche.getBody() == null) {
            FichePatientDTO newFiche = new FichePatientDTO();
            newFiche.setPatientId(id);
            newFiche.setNotes(new ArrayList<>());
            restTemplate.postForEntity(URL_GATEWAY+"/doctor/createFiche/"+id, newFiche, FichePatientDTO.class);
            fiche = restTemplate.exchange(URL_GATEWAY+"/doctor/"+id, HttpMethod.GET, null, FichePatientDTO.class);

        }
        FichePatientDTO fichePatientDTO = fiche.getBody();
        List<NoteDTO> notes = fichePatientDTO.getNotes();
        notes.sort(Comparator.comparing(NoteDTO::getDate).reversed());
        model.addAttribute("notes", notes);

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
