package com.example.petitionplatform.controller;


import com.example.petitionplatform.dto.MessageResponse;
import com.example.petitionplatform.dto.PetitionDTO;
import com.example.petitionplatform.dto.PetitionResponseRequest;
import com.example.petitionplatform.dto.ThresholdRequest;
import com.example.petitionplatform.service.CommitteeService;
import com.example.petitionplatform.service.PetitionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class CommitteeController{
    private final PetitionService petitionService;
    private final CommitteeService committeeService;

    @Autowired
    public CommitteeController(PetitionService petitionService, CommitteeService committeeService) {
        this.petitionService = petitionService;
        this.committeeService = committeeService;
    }

    @GetMapping("/threshold")
    public ResponseEntity<Map<String, Integer>> getThreshold() {
        return ResponseEntity.ok(Collections.singletonMap("threshold",
                committeeService.getSignatureThreshold()));
    }

    @PostMapping("/threshold")
    public ResponseEntity<?> setThreshold(@RequestBody @Valid ThresholdRequest request) {
        committeeService.setSignatureThreshold(request.getThreshold());
        return ResponseEntity.ok(new MessageResponse("Threshold updated successfully"));
    }

    @GetMapping("/petitions")
    public ResponseEntity<List<PetitionDTO>> getAllPetitions() {
        return ResponseEntity.ok(petitionService.getAllPetitionsForCommittee());
    }

    @PostMapping("/petitions/{id}/respond")
    public ResponseEntity<?> respondToPetition(
            @PathVariable Long id,
            @RequestBody @Valid PetitionResponseRequest request) {
        committeeService.respondToPetition(id, request.getResponse());
        return ResponseEntity.ok(new MessageResponse("Response submitted successfully"));
    }
}
