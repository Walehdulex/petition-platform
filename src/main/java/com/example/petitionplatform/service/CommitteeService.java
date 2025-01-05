package com.example.petitionplatform.service;

import com.example.petitionplatform.exception.ResourceNotFoundException;
import com.example.petitionplatform.model.Petition;
import com.example.petitionplatform.model.PetitionStatus;
import com.example.petitionplatform.repository.PetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommitteeService {
    private final PetitionRepository petitionRepository;
    private Integer signatureThreshold = 100; // Default threshold

    @Autowired
    public CommitteeService(PetitionRepository petitionRepository) {
        this.petitionRepository = petitionRepository;
    }

    public int getSignatureThreshold() {
        return signatureThreshold;
    }

    public void setSignatureThreshold(int threshold) {
        if (threshold < 1) {
            throw new IllegalArgumentException("Threshold must be greater than 0");
        }
        this.signatureThreshold = threshold;
    }

    public void respondToPetition(Long petitionId, String response) {
        Petition petition = petitionRepository.findById(petitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Petition not found"));

        if (petition.getSignatures().size() < signatureThreshold) {
            throw new IllegalStateException("Petition has not met the signature threshold");
        }

        petition.setResponse(response);
        petition.setStatus(PetitionStatus.CLOSED);
        petitionRepository.save(petition);
    }
}
