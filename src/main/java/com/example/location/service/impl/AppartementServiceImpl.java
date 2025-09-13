package com.example.location.service.impl;

import com.example.location.dao.AppartementDao;
import com.example.location.dao.impl.AppartementDaoJpa;
import com.example.location.entity.Appartement;
import com.example.location.service.AppartementService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AppartementServiceImpl implements AppartementService {

    private final AppartementDao dao;

    public AppartementServiceImpl() { this(new AppartementDaoJpa()); }
    public AppartementServiceImpl(AppartementDao dao) { this.dao = dao; }

    @Override
    public Appartement save(Appartement a) { return dao.save(a); }

    @Override
    public Appartement update(Long id, Appartement a) {
        a.setId(id);
        return dao.update(a);
    }

    @Override
    public void deleteById(Long id) { dao.deleteById(id); }

    @Override
    public Optional<Appartement> findById(Long id) { return dao.findById(id); }

    @Override
    public List<Appartement> findAll() { return dao.findAll(); }

    @Override
    public List<Appartement> findByImmeubleId(Long immeubleId) { return dao.findByImmeubleId(immeubleId); }

    // ✅ Étape 2
    @Override
    public List<Appartement> searchFiltered(BigDecimal min, BigDecimal max, String ville, Integer nbPieces) {
        return dao.searchFiltered(min, max, ville, nbPieces);
    }
}
