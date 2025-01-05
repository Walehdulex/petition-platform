package com.example.petitionplatform.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "petitions")
@NoArgsConstructor
public class Petition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "petition_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PetitionStatus status = PetitionStatus.OPEN;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Petitioner creator;

    @ManyToMany
    @JoinTable(
            name = "petition_signatures",
            joinColumns = @JoinColumn(name = "petition_id"),
            inverseJoinColumns = @JoinColumn(name = "petitioner_id")
    )
    private Set<Petitioner> signatures = new HashSet<>();

    @Column(name = "response")
    private String response;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public PetitionStatus getStatus() {
        return status;
    }

    public Petitioner getCreator() {
        return creator;
    }

    public Set<Petitioner> getSignatures() {
        return signatures;
    }

    public String getResponse() {
        return response;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(PetitionStatus status) {
        this.status = status;
    }

    public void setCreator(Petitioner creator) {
        this.creator = creator;
    }

    public void setSignatures(Set<Petitioner> signatures) {
        this.signatures = signatures;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method
    public void addSignature(Petitioner petitioner) {
        if (this.signatures == null) {
            this.signatures = new HashSet<>();
        }
        this.signatures.add(petitioner);
    }
}