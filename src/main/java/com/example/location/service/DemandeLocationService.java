package com.example.location.service;

import com.example.location.entity.DemandeLocation;
import com.example.location.entity.DemandeStatut;

import java.util.List;
import java.util.Optional;

public interface DemandeLocationService {
    DemandeLocation create(Long appartementId, Long locataireUserId, String message);
    DemandeLocation updateStatut(Long demandeId, DemandeStatut statut);

    Optional<DemandeLocation> findById(Long id);
    List<DemandeLocation> findAll();
    List<DemandeLocation> findByLocataire(Long userId);

    // Filtrage propriétaire (ne voir que ses demandes)
    List<DemandeLocation> listForProprietaire(Long proprietaireId);

    // Raccourci si tu veux conserver les deux signatures
    List<DemandeLocation> listAll();
}
