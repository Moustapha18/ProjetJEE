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
            em.getTransaction().begin();

            // Propriétaire par défaut
            boolean ownerExists = !em.createQuery(
                            "SELECT u FROM Utilisateur u WHERE u.role = :r", Utilisateur.class)
                    .setParameter("r", Role.PROPRIETAIRE)
                    .getResultList().isEmpty();

            if (!ownerExists) {
                Utilisateur owner = new Utilisateur();
                owner.setUsername("owner1");
                owner.setEmail("owner1@demo.sn");
                owner.setPasswordHash(PasswordHasher.hash("password"));
                owner.setRole(Role.PROPRIETAIRE);
                em.persist(owner);
                System.out.println("[Bootstrap] Propriétaire seedé: owner1 / password");
            }

            // Locataire par défaut
            boolean tenantExists = !em.createQuery(
                            "SELECT u FROM Utilisateur u WHERE u.role = :r", Utilisateur.class)
                    .setParameter("r", Role.LOCATAIRE)
                    .getResultList().isEmpty();

            if (!tenantExists) {
                Utilisateur tenant = new Utilisateur();
                tenant.setUsername("client1");
                tenant.setEmail("client1@demo.sn");
                tenant.setPasswordHash(PasswordHasher.hash("password"));
                tenant.setRole(Role.LOCATAIRE);
                em.persist(tenant);
                System.out.println("[Bootstrap] Locataire seedé: client1 / password");
            }

            // Admin principal par défaut
            boolean adminExists = !em.createQuery(
                            "SELECT u FROM Utilisateur u WHERE u.role = :r", Utilisateur.class)
                    .setParameter("r", Role.ADMIN)
                    .getResultList().isEmpty();

            if (!adminExists) {
                Utilisateur admin = new Utilisateur();
                admin.setUsername("admin");
                admin.setEmail("admin@demo.sn");
                admin.setPasswordHash(PasswordHasher.hash("password"));
                admin.setRole(Role.ADMIN);
                em.persist(admin);
                System.out.println("[Bootstrap] Admin seedé: admin / password");
            }

            // ✅ Ajout d’un deuxième admin si username "admin2" absent
            boolean admin2Exists = !em.createQuery(
                            "SELECT u FROM Utilisateur u WHERE u.username = :username", Utilisateur.class)
                    .setParameter("username", "admin3")
                    .getResultList().isEmpty();

            if (!admin2Exists) {
                Utilisateur admin3 = new Utilisateur();
                admin3.setUsername("admin3");
                admin3.setEmail("admin3@demo.sn");
                admin3.setPasswordHash(PasswordHasher.hash("admin3"));
                admin3.setRole(Role.ADMIN);
                em.persist(admin3);
                System.out.println("[Bootstrap] Deuxième admin seedé: admin3 / admin3");
            }

            em.getTransaction().commit();

        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
