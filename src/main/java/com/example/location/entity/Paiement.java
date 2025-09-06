package com.example.location.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // contrat payé
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Column(name = "montant", precision = 12, scale = 2, nullable = false)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", length = 20, nullable = false)
    private ModePaiement mode;

    @Column(name = "reference", length = 80)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20, nullable = false)
    private StatutPaiement statut = StatutPaiement.PAYE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (statut == null) statut = StatutPaiement.PAYE;
    }

    public Paiement() {}

    public Paiement(Long id, Contrat contrat, LocalDate datePaiement, BigDecimal montant,
                    ModePaiement mode, String reference, StatutPaiement statut) {
        this.id = id;
        this.contrat = contrat;
        this.datePaiement = datePaiement;
        this.montant = montant;
        this.mode = mode;
        this.reference = reference;
        this.statut = statut;
    }

    public Long getId() { return id; }
    public Contrat getContrat() { return contrat; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public BigDecimal getMontant() { return montant; }
    public ModePaiement getMode() { return mode; }
    public String getReference() { return reference; }
    public StatutPaiement getStatut() { return statut; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setContrat(Contrat contrat) { this.contrat = contrat; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public void setMode(ModePaiement mode) { this.mode = mode; }
    public void setReference(String reference) { this.reference = reference; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
