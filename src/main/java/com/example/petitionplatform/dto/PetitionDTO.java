package com.example.petitionplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetitionDTO {
    private Long petitionId;
    private String status;
    private String petitionTitle;
    private String PetitionText;
    private String petitioner;
    private int signatures;
    private String response;

    public Long getPetitionId() {
        return petitionId;
    }

    public void setPetitionId(Long petitionId) {
        this.petitionId = petitionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPetitionTitle() {
        return petitionTitle;
    }

    public void setPetitionTitle(String petitionTitle) {
        this.petitionTitle = petitionTitle;
    }

    public String getPetitionText() {
        return PetitionText;
    }

    public void setPetitionText(String petitionText) {
        PetitionText = petitionText;
    }

    public String getPetitioner() {
        return petitioner;
    }

    public void setPetitioner(String petitioner) {
        this.petitioner = petitioner;
    }

    public int getSignatures() {
        return signatures;
    }

    public void setSignatures(int signatures) {
        this.signatures = signatures;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
