package com.example.location.dao;

import com.example.location.entity.Paiement;
import com.example.location.entity.PaiementStatut;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaiementDao {
    Paiement save(Paiement p);
    Paiement update(Paiement p);
    Optional<Paiement> findById(Long id);
    void deleteById(Long id);

    List<Paiement> findAll();
    List<Paiement> findByContratId(Long contratId);
    List<Paiement> findByLocataireEmail(String email);
    Optional<Paiement> findByContratAndEcheance(Long contratId, LocalDate echeance);
    List<Paiement> findByStatut(PaiementStatut statut);
}
