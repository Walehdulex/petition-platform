package com.example.petitionplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping
public class OpenDataController {
    @Autowired
    private PetitionService petitionService;

    @GetMapping("/petitions")
    public ResponseEntity<Map<String, List<PetitionDTO>>> getPetitions(
            @RequestParam(required = false) String status) {
        List<PetitionDTO> petitions = petitionService.getPetitionsForOpenData(status);
        return ResponseEntity.ok(Collections.singletonMap("petitions", petitions));
    }
}
