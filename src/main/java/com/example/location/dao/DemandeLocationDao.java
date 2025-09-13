package com.example.location.dao;

import com.example.location.entity.DemandeLocation;

import java.util.List;
import java.util.Optional;

public interface DemandeLocationDao {
    DemandeLocation save(DemandeLocation d);
    DemandeLocation update(DemandeLocation d);
    Optional<DemandeLocation> findById(Long id);
    List<DemandeLocation> findAll();

    // Locataire (côté utilisateur)
    List<DemandeLocation> findByLocataireId(Long userId);

    // Prévenir les doublons : demande EN_ATTENTE pour (appartement, utilisateur)
    Optional<DemandeLocation> findPendingByAppAndUser(Long appartementId, Long userId);

    // Propriétaire : ne voir que les demandes liées à SES immeubles
    List<DemandeLocation> findByProprietaireId(Long proprietaireId);
}
