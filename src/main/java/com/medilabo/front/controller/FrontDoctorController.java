package com.medilabo.front.controller;

import com.medilabo.front.model.NoteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

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


    @GetMapping("/addNote/{id}")
    public String getAddNote(@PathVariable Integer id, Model model) {
        model.addAttribute("note", new NoteDTO());
        model.addAttribute("patientId", id);
        return "addNote";
    }

    @PostMapping("/{id}")
    public String addNoteToFiche(@PathVariable Integer id, NoteDTO note) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            logger.info("jusque la ca va");
            Date parsedDate = sdf.parse(note.getDateAsString());
            note.setDate(parsedDate);
        } catch (ParseException e) {
            logger.info("Date conversion failed !");
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
        logger.info("ici on est ok");
        restTemplate.postForEntity(URL_GATEWAY+"/doctor/"+id, note, NoteDTO.class);
        logger.info("post note done");

        return "redirect:/patients/"+id;
    }




}
