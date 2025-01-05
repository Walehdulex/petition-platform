package com.example.petitionplatform.security;

import com.example.petitionplatform.model.Petitioner;
import com.example.petitionplatform.repository.PetitionerRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
@Component
public class SecurityUtils {

    @Autowired
    private PetitionerRepository petitionerRepository;

    public Petitioner getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return petitionerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
