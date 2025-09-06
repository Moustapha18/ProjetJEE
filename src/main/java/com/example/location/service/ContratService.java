package com.example.location.service;

import com.example.location.entity.Contrat;
import com.example.location.entity.StatutContrat;

import java.util.List;
import java.util.Optional;

public interface ContratService {
    Contrat save(Contrat c);
    Contrat update(Long id, Contrat data);
    void deleteById(Long id);
    Optional<Contrat> findById(Long id);
    List<Contrat> findAll();
    List<Contrat> findByStatut(StatutContrat statut);
}
