package com.example.location.service;

import com.example.location.entity.Contrat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ContratService {
    Optional<Contrat> findById(Long id);

    // Résilier un contrat : fixe la dateFin et passe le statut à résilié/inactif
    Contrat terminate(Long contratId, LocalDate endDate);

    // Renouveler (ou réactiver) : remet en ACTIF, peut ajuster le loyer et (re)définir dateDebut
    Contrat renew(Long contratId, LocalDate newStartDate, BigDecimal newLoyer);

    // Génère l’échéancier mensuel (paiements EN_ATTENTE) sur une période
    void ensureSchedule(Long contratId, LocalDate startInclusive, LocalDate endInclusive);
}
