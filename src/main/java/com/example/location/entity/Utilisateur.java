package com.example.location.entity;

import com.example.location.security.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "utilisateurs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_utilisateur_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_utilisateur_email", columnNames = "email")
        }
)
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String username;

    @Email @NotBlank
    @Column(nullable = false, length = 180)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.LOCATAIRE;

    // --- Champs reset MDP : UNIQUEMENT des colonnes simples ---
    @Column(name = "reset_token", length = 64)
    private String resetToken;

    @Column(name = "reset_expires")
    private LocalDateTime resetExpires;

    // Relation inverse (optionnelle)
    @OneToMany(mappedBy = "proprietaire", fetch = FetchType.LAZY)
    @OrderBy("id desc")
    private List<Immeuble> immeubles = new ArrayList<>();

    // --- Hooks ---
    @PrePersist @PreUpdate
    private void normalize() {
        if (email != null) email = email.trim().toLowerCase();
        if (username != null) username = username.trim();
    }

    // --- Getters / Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        if (this.email != null) this.email = this.email.trim().toLowerCase();
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<Immeuble> getImmeubles() { return immeubles; }
    public void setImmeubles(List<Immeuble> immeubles) {
        this.immeubles = (immeubles != null) ? immeubles : new ArrayList<>();
    }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getResetExpires() { return resetExpires; }
    public void setResetExpires(LocalDateTime resetExpires) { this.resetExpires = resetExpires; }
}
