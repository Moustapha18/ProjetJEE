package com.example.location.service.impl;

import com.example.location.dao.LocataireDao;
import com.example.location.dao.impl.LocataireDaoJpa;
import com.example.location.entity.Locataire;
import com.example.location.service.LocataireService;

import java.util.List;
import java.util.Optional;

public class LocataireServiceImpl implements LocataireService {

    private final LocataireDao dao = new LocataireDaoJpa();

    @Override
    public List<Locataire> findAll() { return dao.findAll(); }

    @Override
    public Optional<Locataire> findById(Long id) { return dao.findById(id); }

    @Override
    public Locataire save(Locataire l) { return dao.save(l); }

    @Override
    public Locataire update(Long id, Locataire l) { return dao.update(id, l); }

    @Override
    public void deleteById(Long id) { dao.deleteById(id); }
}
