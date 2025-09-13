package com.example.location.service;

import com.example.location.entity.Appartement;
import com.example.location.entity.Immeuble;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ImmeubleService {
    List<Immeuble> findAll();
    Optional<Immeuble> findById(Long id);
    Immeuble save(Immeuble i);
    Immeuble update(Long id, Immeuble data);
    void deleteById(Long id);

    Object findAllOrderByIdDesc();

    // src/main/java/com/example/location/service/ImmeubleService.java
// ajoute :
    List<Immeuble> findByProprietaireId(Long userId);

    //
    //  List<Appartement> searchFiltered(BigDecimal min, BigDecimal max, String ville, Integer nbPieces);
}
