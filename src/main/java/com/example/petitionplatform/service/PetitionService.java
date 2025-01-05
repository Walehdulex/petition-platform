package com.example.petitionplatform.service;

import com.example.petitionplatform.dto.PetitionDTO;
import com.example.petitionplatform.dto.PetitionRequest;
import com.example.petitionplatform.model.Petition;
import com.example.petitionplatform.model.PetitionStatus;
import com.example.petitionplatform.model.Petitioner;
import com.example.petitionplatform.repository.PetitionRepository;
import com.example.petitionplatform.repository.PetitionerRepository;
import com.example.petitionplatform.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PetitionService {
    private final PetitionRepository petitionRepository;
    private final PetitionerRepository petitionerRepository;
    private final SecurityUtils securityUtils;

    public PetitionService(PetitionRepository petitionRepository,
                           PetitionerRepository petitionerRepository,
                           SecurityUtils securityUtils) {
        this.petitionerRepository = petitionerRepository;
        this.petitionRepository = petitionRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public Petition createPetition(PetitionRequest request) {
        Petitioner creator = securityUtils.getCurrentUser();

        Petition petition = new Petition();
        petition.setTitle(request.getTitle());
        petition.setContent(request.getContent());
        petition.setCreator(creator);

        return petitionRepository.save(petition);
    }

    @Transactional
    public ResponseEntity<?> signPetition(Long petitionId) {
        Petition petition = petitionRepository.findById(petitionId)
                .orElseThrow(() -> new RuntimeException("Petition not found"));

        if (petition.getStatus() == PetitionStatus.CLOSED) {
            throw new IllegalStateException("Petition is closed");
        }

        Petitioner signer = securityUtils.getCurrentUser();
        if (petition.getSignatures().contains(signer)) {
            throw new IllegalStateException("Petition is already signed");
        }

        petition.getSignatures().add(signer);
        petitionRepository.save(petition);

        return ResponseEntity.ok().build();
    }

//    public List<PetitionDTO> getPetitionsForOpenData(String status) {
//        List<Petition> petitions;
//        if (status != null) {
//            petitions = petitionRepository.findByStatus(status);
//        } else {
//            petitions = petitionRepository.findAll();
//        }
//
//        return petitions.stream()
//                .map(this::convertToPetitionDTO)
//                .collect(Collectors.toList());
//    }

    public List<PetitionDTO> getPetitionsForOpenData(String status) {
        List<Petition> petitions;

        if (status != null) {
            try {
                PetitionStatus petitionStatus = PetitionStatus.valueOf(status.toUpperCase());
                petitions = petitionRepository.findByStatus(petitionStatus);
            } catch (IllegalArgumentException e) {
                // Invalid status provided
                petitions = new ArrayList<>();
            }
        } else {
            petitions = petitionRepository.findAll();
        }

        return petitions.stream()
                .map(this::convertToPetitionDTO)  // Using the existing conversion method
                .collect(Collectors.toList());
    }


//    public List<PetitionDTO> getPetitionsForOpenData(String status) {
//        List<Petition> petitions;
//
//        // If status is provided, filter by status
//        if (status != null) {
//            if (status.equals("OPEN")) {
//                petitions = petitionRepository.findByStatus(PetitionStatus.OPEN);
//            } else if (status.equals("CLOSED")) {
//                petitions = petitionRepository.findByStatus(PetitionStatus.CLOSED);
//            } else {
//                // Invalid status provided, return empty list
//                petitions = new ArrayList<>();
//            }
//        } else {
//            // No status provided, return all petitions
//            petitions = petitionRepository.findAll();
//        }
//
//        return petitions.stream()
//                .map(this::convertToPetitionDTO)
//                .collect(Collectors.toList());
//    }


    private PetitionDTO convertToPetitionDTO(Petition petition) {
        PetitionDTO dto = new PetitionDTO();
        dto.setPetitionId(petition.getId());
        dto.setStatus(petition.getStatus().toString());
        dto.setPetitionTitle(petition.getTitle());
        dto.setPetitionText(petition.getContent());
        dto.setPetitioner(petition.getCreator().getUsername()); // Using getUsername() which returns email
        dto.setSignatures(petition.getSignatures().size());
        dto.setResponse(petition.getResponse());
        return dto;
    }

    public List<Petition> getAllPetitions() {
        return petitionRepository.findAll();
    }

    public Petition getPetitionById(Long id) {
        return petitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Petition not found"));
    }

    public List<PetitionDTO> getAllPetitionsForCommittee() {
        List<Petition> petitions = petitionRepository.findAll();
        return petitions.stream()
                .map(this::convertToPetitionDTO)
                .collect(Collectors.toList());
    }

}
