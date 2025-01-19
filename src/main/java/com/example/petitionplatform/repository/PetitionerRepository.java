package com.example.petitionplatform.repository;

import com.example.petitionplatform.model.Petitioner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetitionerRepository extends JpaRepository<Petitioner, Long> {
    Optional<Petitioner> findByEmail(String email);
    boolean existsByBioId(String bioId);
    boolean existsByEmail(String email);
    Optional<Petitioner> findByEmailAndBioId(String email, String bioId);

}
