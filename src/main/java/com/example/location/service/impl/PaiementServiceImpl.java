package com.example.location.service.impl;

import com.example.location.dao.PaiementDao;
import com.example.location.dao.impl.PaiementDaoJpa;
import com.example.location.entity.Paiement;
import com.example.location.service.PaiementService;

import java.util.List;
import java.util.Optional;

public class PaiementServiceImpl implements PaiementService {

    private final PaiementDao dao = new PaiementDaoJpa();

    @Override
    public List<Paiement> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<Paiement> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public Paiement save(Paiement p) {
        return dao.save(p);
    }

    @Override
    public Paiement update(Long id, Paiement data) {
        Paiement ex = dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Paiement introuvable"));
        ex.setContrat(data.getContrat());
        ex.setDatePaiement(data.getDatePaiement());
        ex.setMontant(data.getMontant());
        ex.setMode(data.getMode());
        ex.setReference(data.getReference());
        ex.setStatut(data.getStatut());
        return dao.save(ex); // ✅ remplace dao.update(ex)
    }


    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
