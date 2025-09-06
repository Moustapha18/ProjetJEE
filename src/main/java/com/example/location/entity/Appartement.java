package com.example.location.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "appartements")
public class Appartement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String numero;

    private Integer etage;

    @Min(0)
    private Double surface;   // m²

    @Min(0)
    private Double loyer;     // montant

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "immeuble_id", nullable = false)
    @NotNull
    private Immeuble immeuble;

    public Appartement() {}
    public Appartement(Long id, String numero, Integer etage, Double surface, Double loyer, Immeuble immeuble) {
        this.id = id; this.numero = numero; this.etage = etage; this.surface = surface; this.loyer = loyer; this.immeuble = immeuble;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public Integer getEtage() { return etage; }
    public void setEtage(Integer etage) { this.etage = etage; }
    public Double getSurface() { return surface; }
    public void setSurface(Double surface) { this.surface = surface; }
    public Double getLoyer() { return loyer; }
    public void setLoyer(Double loyer) { this.loyer = loyer; }
    public Immeuble getImmeuble() { return immeuble; }
    public void setImmeuble(Immeuble immeuble) { this.immeuble = immeuble; }
}
