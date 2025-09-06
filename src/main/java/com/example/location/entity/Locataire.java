package com.example.location.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "locataires", uniqueConstraints = {
        @UniqueConstraint(name = "uk_locataire_email", columnNames = "email")
})
public class Locataire {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nomComplet;

    @Email
    private String email;

    private String telephone;

    public Locataire() {}

    public Locataire(Long id, String nomComplet, String email, String telephone) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.email = email;
        this.telephone = telephone;
    }

    public Long getId() { return id; }
    public String getNomComplet() { return nomComplet; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }

    public void setId(Long id) { this.id = id; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public void setEmail(String email) { this.email = email; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
