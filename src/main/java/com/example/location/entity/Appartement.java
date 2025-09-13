package com.example.location.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "appartements")
public class Appartement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private Integer etage;

    // Selon ton modèle : BigDecimal ou Double ; on garde BigDecimal
    private BigDecimal loyer;

    private Double surface;

    @Column(name = "nb_pieces")
    private Integer nbPieces;

    @ManyToOne(optional = false)
    @JoinColumn(name = "immeuble_id")
    private Immeuble immeuble;

    /** Flags calculés coté DAO (pas persistés) */
    @Transient
    private boolean occupe;   // vrai si contrat ACTIF (dateFin NULL) existe
    @Transient
    private boolean reserve;  // vrai si une DemandeLocation EN_ATTENTE existe

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Integer getEtage() { return etage; }
    public void setEtage(Integer etage) { this.etage = etage; }

    public BigDecimal getLoyer() { return loyer; }
    public void setLoyer(BigDecimal loyer) { this.loyer = loyer; }

    public Double getSurface() { return surface; }
    public void setSurface(Double surface) { this.surface = surface; }

    public Integer getNbPieces() { return nbPieces; }
    public void setNbPieces(Integer nbPieces) { this.nbPieces = nbPieces; }

    public Immeuble getImmeuble() { return immeuble; }
    public void setImmeuble(Immeuble immeuble) { this.immeuble = immeuble; }

    public boolean isOccupe() { return occupe; }
    public void setOccupe(boolean occupe) { this.occupe = occupe; }

    public boolean isReserve() { return reserve; }
    public void setReserve(boolean reserve) { this.reserve = reserve; }
}
