package com.example.location.config;

import com.example.location.entity.Utilisateur;
import com.example.location.security.PasswordHasher;

import com.example.location.security.Role;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.List;

@WebListener
public class BootstrapListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            // Cherche un admin existant
            List<Utilisateur> admins = em.createQuery(
                            "select u from Utilisateur u where u.role = :r", Utilisateur.class)
                    .setParameter("r", Role.ADMIN)
                    .getResultList();

            if (admins.isEmpty()) {
                em.getTransaction().begin();
                Utilisateur admin = new Utilisateur();
                admin.setUsername("admin");
                admin.setEmail("admin@demo.sn");
                admin.setPasswordHash(PasswordHasher.hash("password")); // à changer !
                admin.setRole(Role.ADMIN);
                em.persist(admin);
                em.getTransaction().commit();
                System.out.println("[Bootstrap] Admin seed: admin / password");
            }
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
