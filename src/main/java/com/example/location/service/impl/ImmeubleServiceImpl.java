package com.example.location.service.impl;

import com.example.location.config.JpaUtil;
import com.example.location.entity.Immeuble;
import com.example.location.service.ImmeubleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import com.example.location.entity.Utilisateur;

public class ImmeubleServiceImpl implements ImmeubleService {

    @Override
    public List<Immeuble> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                    "select i from Immeuble i order by i.id desc", Immeuble.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Immeuble> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Immeuble.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Immeuble save(Immeuble i) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (i.getProprietaire() != null && i.getProprietaire().getId() != null) {
                Utilisateur managed = em.getReference(Utilisateur.class, i.getProprietaire().getId());
                i.setProprietaire(managed);
            }
            em.persist(i);
            tx.commit();
            return i;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Immeuble update(Long id, Immeuble data) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Immeuble ex = em.find(Immeuble.class, id);
            if (data.getProprietaire() != null && data.getProprietaire().getId() != null) {
                Utilisateur managed = em.getReference(Utilisateur.class, data.getProprietaire().getId());
                ex.setProprietaire(managed);
            } else {
                ex.setProprietaire(null);
            }
            if (ex == null) throw new IllegalArgumentException("Immeuble introuvable");

            ex.setNom(data.getNom());
            ex.setAdresse(data.getAdresse());
            ex.setVille(data.getVille());
            ex.setCodePostal(data.getCodePostal());
            ex.setDescription(data.getDescription());
            ex.setAnneeConstruction(data.getAnneeConstruction());
            ex.setNbEtages(data.getNbEtages());
            ex.setNbAppartements(data.getNbAppartements());
            ex.setSurfaceTotale(data.getSurfaceTotale());
            // si tu gères l’assignation du propriétaire ici :
            ex.setProprietaire(data.getProprietaire());

            Immeuble merged = em.merge(ex);
            tx.commit();
            return merged;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Immeuble ref = em.find(Immeuble.class, id);
            if (ref != null) em.remove(ref);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Immeuble> findByProprietaireId(Long userId) {
        var em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
            select i from Immeuble i
            where i.proprietaire.id = :uid
            order by i.id desc
        """, Immeuble.class)
                    .setParameter("uid", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    // Si du code appelle encore cette méthode, on retourne simplement findAll()
    public List<Immeuble> findAllOrderByIdDesc() {
        return findAll(); // ou recopie la même JPQL avec order by i.id desc
    }

}
