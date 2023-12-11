package com.medilabo.front.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class PatientDTO {

    private Integer idPatient;

    private String nom;

    private String prenom;

    private String genre;

    private String rue;

    private String codePostal;

    private String ville;

    private String telephone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateNaissance;

}
