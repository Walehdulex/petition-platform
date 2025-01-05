package com.example.petitionplatform.controller;

import com.example.petitionplatform.dto.PetitionDTO;
import com.example.petitionplatform.service.PetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/slpp")
public class OpenDataController {
    private final PetitionService petitionService;

    public OpenDataController(PetitionService petitionService) {
        this.petitionService = petitionService;
    }

//    @GetMapping("/petitions")
//    public ResponseEntity<Map<String, List<PetitionDTO>>> getPetitions(
//            @RequestParam(required = false) String status) {
//        List<PetitionDTO> petitions = petitionService.getPetitionsForOpenData(status);
//        return ResponseEntity.ok(Collections.singletonMap("petitions", petitions));
//    }

    @GetMapping("/petitions")
    public ResponseEntity<?> getAllPetitions(
            @RequestParam(required = false) String status) {
        String normalizedStatus = (status != null) ? status.toUpperCase() : null;
        List<PetitionDTO> petitions = petitionService.getPetitionsForOpenData(status);
        Map<String, List<PetitionDTO>> response = new HashMap<>();
        response.put("petitions", petitions);
        return ResponseEntity.ok(response);
    }
}
