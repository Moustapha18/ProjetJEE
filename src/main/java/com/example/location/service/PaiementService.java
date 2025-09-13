package com.example.location.service;

import com.example.location.entity.Contrat;
import com.example.location.entity.Paiement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaiementService {
    List<Paiement> findAll();
    Optional<Paiement> findById(Long id);

    // Met à jour EN_ATTENTE / RETARD / PAYE selon datePaiement / échéance
    void refreshStatuses();

    // Crée N échéances mensuelles à partir de la date de début du contrat
    void generateScheduleForContract(Contrat c, int months);

    // Crée les échéances pour un contrat entre deux dates (inclusives)
    void ensureScheduleForContract(Long contratId, LocalDate startInclusive, LocalDate endInclusive);

    // Marque un paiement comme payé
    Paiement markPaid(Long paiementId, String reference, LocalDate paidAt);
}
