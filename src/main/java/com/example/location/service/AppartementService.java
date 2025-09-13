package com.example.location.service;

import com.example.location.entity.Appartement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AppartementService {
    Appartement save(Appartement a);
    Appartement update(Long id, Appartement a);
    void deleteById(Long id);
    Optional<Appartement> findById(Long id);
    List<Appartement> findAll();
    List<Appartement> findByImmeubleId(Long immeubleId);

    // ✅ Ajout pour l’Étape 2
    List<Appartement> searchFiltered(BigDecimal min, BigDecimal max, String ville, Integer nbPieces);
}
