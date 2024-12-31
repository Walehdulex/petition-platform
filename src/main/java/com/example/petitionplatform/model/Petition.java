package com.example.petitionplatform.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "petition")
public class Petition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    private PetitionStatus status = PetitionStatus.OPEN;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Petitioner creator;

    @ManyToMany
    @JoinTable(name = "petition_signatures")
    private Set<Petitioner> signatures = new HashSet<>();

    private String response;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


    public Petition(Long id, String title, String content, PetitionStatus status, Petitioner creator, Set<Petitioner> signatures, String response, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.creator = creator;
        this.signatures = signatures;
        this.response = response;
        this.createdAt = createdAt;
    }

    public Petition() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PetitionStatus getStatus() {
        return status;
    }

    public void setStatus(PetitionStatus status) {
        this.status = status;
    }

    public Petitioner getCreator() {
        return creator;
    }

    public void setCreator(Petitioner creator) {
        this.creator = creator;
    }

    public Set<Petitioner> getSignatures() {
        return signatures;
    }

    public void setSignatures(Set<Petitioner> signatures) {
        this.signatures = signatures;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
