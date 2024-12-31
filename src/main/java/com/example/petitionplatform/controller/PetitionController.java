package com.example.petitionplatform.controller;

import com.example.petitionplatform.model.Petition;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PetitionController {
    @Autowired
    private PetitionService petitionService;

    @PostMapping("/petitions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Petition> createPetition(@Valid @RequestBody PetitionRequest request) {
        return ResponseEntity.ok(petitionService.createPetition(request));
    }

    @PostMapping("/petitions/{id}/sign")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> signPetition(@PathVariable Long id) {
        return petitionService.signPetition(id);
    }

    @GetMapping("/petitions")
    public ResponseEntity<List<Petition>> getAllPetitions() {
        return ResponseEntity.ok(petitionService.getAllPetitions());
    }
}
