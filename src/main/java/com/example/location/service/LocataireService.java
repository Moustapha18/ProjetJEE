package com.example.location.service;

import com.example.location.entity.Locataire;

import java.util.List;
import java.util.Optional;

public interface LocataireService {
    List<Locataire> findAll();
    Optional<Locataire> findById(Long id);
    Locataire save(Locataire l);
    Locataire update(Long id, Locataire l);
    void deleteById(Long id);
}
