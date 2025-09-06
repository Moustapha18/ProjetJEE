package com.example.location.dao;

import com.example.location.entity.Appartement;

import java.util.List;
import java.util.Optional;

public interface AppartementDao {
    Appartement save(Appartement a);
    Appartement update(Appartement a);
    void deleteById(Long id);
    Optional<Appartement> findById(Long id);
    List<Appartement> findAll();
    List<Appartement> findByImmeubleId(Long immeubleId);
}
