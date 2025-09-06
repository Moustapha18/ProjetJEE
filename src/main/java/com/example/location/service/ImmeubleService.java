package com.example.location.service;

import com.example.location.entity.Immeuble;
import java.util.List;
import java.util.Optional;

public interface ImmeubleService {
    List<Immeuble> findAll();
    Optional<Immeuble> findById(Long id);
    Immeuble save(Immeuble i);
    Immeuble update(Long id, Immeuble data);
    void deleteById(Long id);
}
