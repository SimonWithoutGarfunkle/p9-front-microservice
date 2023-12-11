package com.medilabo.front.controller;

import com.medilabo.front.model.NoteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import static com.medilabo.front.Constants.URL_GATEWAY;

/**
 * Send requests to Doctor Back
 */
@Controller
@RequestMapping("/doctor")
public class FrontDoctorController {

    private static Logger logger = LoggerFactory.getLogger(FrontDoctorController.class);

    private final RestTemplate restTemplate;

    @Autowired
    public FrontDoctorController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * Display the form to add a new note to the patient
     * @param id of the patient
     * @param model new note
     * @return page addNote
     */
    @GetMapping("/addNote/{id}")
    public String getAddNote(@PathVariable Integer id, Model model) {
        model.addAttribute("note", new NoteDTO());
        model.addAttribute("patientId", id);
        return "addNote";
    }

    /**
     * Send the form as a new note on the patient
     *
     * @param id of the patient
     * @param note to save
     * @return page patient with the new note
     */
    @PostMapping("/{id}")
    public String addNoteToFiche(@PathVariable Integer id, NoteDTO note) {

        logger.info("sending note from "+note.getDocteur()+" to back doctor");
        restTemplate.postForEntity(URL_GATEWAY+"/doctor/"+id, note, NoteDTO.class);
        logger.info("post note done");

        return "redirect:/patients/"+id;
    }

}
