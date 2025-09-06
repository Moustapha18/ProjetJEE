package com.example.location.service.impl;

import com.example.location.dao.ContratDao;
import com.example.location.dao.impl.ContratDaoJpa;
import com.example.location.entity.Contrat;
import com.example.location.entity.StatutContrat;
import com.example.location.service.ContratService;

import java.util.List;
import java.util.Optional;

public class ContratServiceImpl implements ContratService {

    private final ContratDao dao = new ContratDaoJpa();

    @Override
    public Contrat save(Contrat c) { return dao.save(c); }

    @Override
    public Contrat update(Long id, Contrat data) {
        Contrat existing = dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Contrat introuvable"));
        existing.setAppartement(data.getAppartement());
        existing.setLocataire(data.getLocataire());
        existing.setDateDebut(data.getDateDebut());
        existing.setDateFin(data.getDateFin());
        existing.setLoyerMensuel(data.getLoyerMensuel());
        existing.setCaution(data.getCaution());
        existing.setStatut(data.getStatut());
        return dao.update(existing);
    }

    @Override
    public void deleteById(Long id) { dao.deleteById(id); }

    @Override
    public Optional<Contrat> findById(Long id) { return dao.findById(id); }

    @Override
    public List<Contrat> findAll() { return dao.findAll(); }

    @Override
    public List<Contrat> findByStatut(StatutContrat statut) { return dao.findByStatut(statut); }
}
