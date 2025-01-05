package com.example.petitionplatform.service;

import com.example.petitionplatform.model.Petitioner;
import com.example.petitionplatform.repository.PetitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PetitionerRepository petitionerRepository;

    public CustomUserDetailsService(PetitionerRepository petitionerRepository) {
        this.petitionerRepository = petitionerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Petitioner petitioner = petitionerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Petitioner not found with email: " + email));

        return User.builder()
                .username(petitioner.getEmail())
                .password(petitioner.getPassword()) // You might need to hash this depending on your setup
                .roles("USER")  // You may want to adjust roles based on your domain
                .build();
    }
}
