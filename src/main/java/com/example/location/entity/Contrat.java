package com.example.location.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrat")
public class Contrat {

    public static Object Statut;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appartement_id", nullable = false)
    private Appartement appartement;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "locataire_id", nullable = false)
    private Locataire locataire;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "loyer_mensuel", precision = 12, scale = 2, nullable = false)
    private BigDecimal loyerMensuel;

    @Column(name = "caution", precision = 12, scale = 2)
    private BigDecimal caution;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20, nullable = false)
    private StatutContrat statut = StatutContrat.ACTIF;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Contrat() {}

    public Contrat(Long id,
                   Appartement appartement,
                   Locataire locataire,
                   LocalDate dateDebut,
                   LocalDate dateFin,
                   BigDecimal loyerMensuel,
                   BigDecimal caution,
                   StatutContrat statut) {
        this.id = id;
        this.appartement = appartement;
        this.locataire = locataire;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.loyerMensuel = loyerMensuel;
        this.caution = caution;
        this.statut = statut;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Appartement getAppartement() { return appartement; }
    public void setAppartement(Appartement appartement) { this.appartement = appartement; }

    public Locataire getLocataire() { return locataire; }
    public void setLocataire(Locataire locataire) { this.locataire = locataire; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public BigDecimal getLoyerMensuel() { return loyerMensuel; }
    public void setLoyerMensuel(BigDecimal loyerMensuel) { this.loyerMensuel = loyerMensuel; }

    public BigDecimal getCaution() { return caution; }
    public void setCaution(BigDecimal caution) { this.caution = caution; }

    public StatutContrat getStatut() { return statut; }
    public void setStatut(StatutContrat statut) { this.statut = statut; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
