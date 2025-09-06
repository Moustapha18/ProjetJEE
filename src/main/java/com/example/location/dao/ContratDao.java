package com.example.location.dao;

import com.example.location.entity.Contrat;
import com.example.location.entity.StatutContrat;

import java.util.List;
import java.util.Optional;

public interface ContratDao {
    Contrat save(Contrat c);
    Contrat update(Contrat c);
    void deleteById(Long id);
    Optional<Contrat> findById(Long id);
    List<Contrat> findAll();
    List<Contrat> findByStatut(StatutContrat statut);
}
