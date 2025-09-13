package com.example.location.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_location")
public class DemandeLocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "appartement_id")
    private Appartement appartement;

    // On référence l’utilisateur (role=LOCATAIRE)
    @ManyToOne(optional = false) @JoinColumn(name = "locataire_id")
    private Utilisateur locataire;

    @Enumerated(EnumType.STRING)
    private DemandeStatut statut = DemandeStatut.EN_ATTENTE;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Appartement getAppartement() { return appartement; }
    public void setAppartement(Appartement appartement) { this.appartement = appartement; }

    public Utilisateur getLocataire() { return locataire; }
    public void setLocataire(Utilisateur locataire) { this.locataire = locataire; }

    public DemandeStatut getStatut() { return statut; }
    public void setStatut(DemandeStatut statut) { this.statut = statut; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
