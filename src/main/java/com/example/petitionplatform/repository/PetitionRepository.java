package com.example.petitionplatform.repository;

import com.example.petitionplatform.model.Petition;
import com.example.petitionplatform.model.PetitionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetitionRepository  extends JpaRepository<Petition, Long> {
//    List<Petition> findByStatus(String status);
List<Petition> findByStatus(PetitionStatus status);

}
