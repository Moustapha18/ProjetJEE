package com.example.location.service;

import com.example.location.entity.Paiement;

import java.util.List;
import java.util.Optional;

public interface PaiementService {
    List<Paiement> findAll();
    Optional<Paiement> findById(Long id);
    Paiement save(Paiement p);
    Paiement update(Long id, Paiement data);
    void deleteById(Long id);
}
