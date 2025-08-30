package com.example.location.service.impl;

import com.example.location.dao.ImmeubleDao;
import com.example.location.dao.impl.ImmeubleDaoJpa;
import com.example.location.dto.ImmeubleDto;
import com.example.location.entity.Immeuble;
import com.example.location.service.ImmeubleService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: logique métier d'Immeuble.
 * DIP: dépend d'une abstraction (ImmeubleDao). L'impl par défaut est injectée simplement ici.
 * OCP: remplaçable par autre impl via constructeur.
 */
public class ImmeubleServiceImpl implements ImmeubleService {

    private final ImmeubleDao dao;

    public ImmeubleServiceImpl() {
        this(new ImmeubleDaoJpa());
    }
    public ImmeubleServiceImpl(ImmeubleDao dao) {
        this.dao = dao;
    }

    @Override public List<ImmeubleDto> lister() {
        return dao.findAll().stream()
                .map(e -> new ImmeubleDto(e.getId(), e.getNom(), e.getAdresse()))
                .collect(Collectors.toList());
    }

    @Override public ImmeubleDto creer(ImmeubleDto dto) {
        Immeuble e = new Immeuble();
        e.setNom(dto.nom());
        e.setAdresse(dto.adresse());
        Immeuble saved = dao.save(e);
        return new ImmeubleDto(saved.getId(), saved.getNom(), saved.getAdresse());
    }
}
