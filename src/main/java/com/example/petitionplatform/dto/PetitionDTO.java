package com.example.petitionplatform.dto;

import lombok.Data;

@Data
public class PetitionDTO {
    private Long petitionId;
    private String status;
    private String petitionTitle;
    private String PetitionText;
    private String petitioner;
    private int signatures;
    private String response;
}
