// src/main/java/com/example/location/entity/Immeuble.java
package com.example.location.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "immeuble")
public class Immeuble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Proprio du bien (compte Utilisateur)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "proprietaire_id") // passe à nullable=false quand toutes tes données sont prêtes
    private Utilisateur proprietaire;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "adresse", nullable = false, length = 255)
    private String adresse;

    @Column(name = "ville", nullable = false, length = 120)
    private String ville;

    @Column(name = "code_postal", length = 20)
    private String codePostal;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "annee_construction")
    private Integer anneeConstruction;

    @Column(name = "nb_etages")
    private Integer nbEtages;

    @Column(name = "nb_appartements")
    private Integer nbAppartements;

    @Column(name = "surface_totale", precision = 12, scale = 2)
    private BigDecimal surfaceTotale;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Immeuble() { }

    public Immeuble(Long id,
                    String nom,
                    String adresse,
                    String ville,
                    String codePostal,
                    String description,
                    Integer anneeConstruction,
                    Integer nbEtages,
                    Integer nbAppartements,
                    BigDecimal surfaceTotale) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.description = description;
        this.anneeConstruction = anneeConstruction;
        this.nbEtages = nbEtages;
        this.nbAppartements = nbAppartements;
        this.surfaceTotale = surfaceTotale;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // ---------- Getters / Setters ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getProprietaire() { return proprietaire; }
    public void setProprietaire(Utilisateur proprietaire) { this.proprietaire = proprietaire; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getAnneeConstruction() { return anneeConstruction; }
    public void setAnneeConstruction(Integer anneeConstruction) { this.anneeConstruction = anneeConstruction; }

    public Integer getNbEtages() { return nbEtages; }
    public void setNbEtages(Integer nbEtages) { this.nbEtages = nbEtages; }

    public Integer getNbAppartements() { return nbAppartements; }
    public void setNbAppartements(Integer nbAppartements) { this.nbAppartements = nbAppartements; }

    public BigDecimal getSurfaceTotale() { return surfaceTotale; }
    public void setSurfaceTotale(BigDecimal surfaceTotale) { this.surfaceTotale = surfaceTotale; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
