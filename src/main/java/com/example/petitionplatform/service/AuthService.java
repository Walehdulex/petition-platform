package com.example.petitionplatform.service;

import com.example.petitionplatform.dto.AuthResponse;
import com.example.petitionplatform.dto.LoginRequest;
import com.example.petitionplatform.dto.MessageResponse;
import com.example.petitionplatform.dto.RegisterRequest;
import com.example.petitionplatform.exception.BioIdInvalidException;
import com.example.petitionplatform.model.Petitioner;
import com.example.petitionplatform.model.Role;
import com.example.petitionplatform.repository.PetitionerRepository;
import com.example.petitionplatform.security.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final PetitionerRepository petitionerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    /*Delete before submitting*/
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    @Autowired
    public AuthService(PetitionerRepository petitionerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.petitionerRepository = petitionerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ResponseEntity<?> register(RegisterRequest request) {
        if (petitionerRepository.existsByBioId(request.getBioId())) {
            throw new BioIdInvalidException("BioID already registered");
        }

        Petitioner petitioner = new Petitioner();
        petitioner.setEmail(request.getEmail());
        petitioner.setPassword(passwordEncoder.encode(request.getPassword()));
        petitioner.setBioId(request.getBioId());
        petitioner.setFullName(request.getFullName());
        petitioner.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        petitioner.setRole(Role.ROLE_USER);

        petitionerRepository.save(petitioner);


        return ResponseEntity.ok(new MessageResponse("Registration Successful"));
    }

    @PostConstruct
    public void init() {
        // Create admin account if it doesn't exist
        if (!petitionerRepository.existsByEmail("admin@petition.parliament.sr")) {
            Petitioner admin = new Petitioner();
            admin.setEmail("admin@petition.parliament.sr");
            admin.setPassword(passwordEncoder.encode("2025%shangrila"));
            admin.setFullName("Committee Admin");
            admin.setBioId("ADMIN000000");
            admin.setDateOfBirth(LocalDate.now());
            admin.setRole(Role.ROLE_ADMIN);
            petitionerRepository.save(admin);
        }
    }

    public ResponseEntity<?> authenticate(LoginRequest request) {
        try {
            System.out.println("Login attempt for email: " + request.getEmail());

            Petitioner petitioner = petitionerRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BioIdInvalidException("Invalid credentials"));

            System.out.println("Found user with email: " + petitioner.getEmail());

            if (!passwordEncoder.matches(request.getPassword(), petitioner.getPassword())) {
                System.out.println("Password mismatch for user: " + request.getEmail());
                throw new BioIdInvalidException("Invalid credentials");
            }

            String token = jwtTokenProvider.generateToken(petitioner.getEmail());
            System.out.println("Token generated successfully");

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("email", petitioner.getUsername());
            response.put("role", petitioner.getRole().name());

            System.out.println("Response created successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Authentication failed: " + e.getMessage()));
        }
    }
}




