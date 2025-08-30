# JEE Location Immeubles – Skeleton (SOLID-ready)

## Lancer en local
1. Crée une base MySQL `jee_location` (UTF8).
2. Configure `src/main/resources/META-INF/persistence.xml` (user/mot de passe).
3. `mvn clean package` → génère `jee-location-immeubles.war`.
4. Dépose le `.war` dans `TOMCAT_HOME/webapps/` et démarre Tomcat.
5. Ouvre `http://localhost:8080/jee-location-immeubles/` → redirection /login → puis /app/immeubles.

## Structure (couches claires)
- `entity/` : modèles JPA
- `dao/` : interfaces + impl JPA, **DIP**
- `service/` : interfaces + impl métier, **SRP / OCP / DIP**
- `web/controllers/` : Servlets **minces** (coordination), **SRP**
- `web/filters/` + `security/` : filtres transverses
- `dto/` : objets d'échange, **ISP**
- `config/` : utilitaires JPA

## SOLID appliqué
- **S (Single Responsibility)** : chaque classe a une seule raison de changer (ex. `ImmeubleServiceImpl` ne fait pas de JDBC).
- **O (Open/Closed)** : services/DAO extensibles sans modifier l’existant (ex. remplacer `ImmeubleDaoJpa` par une autre impl).
- **L (Liskov Substitution)** : `CrudDao` impose un contrat simple, toutes les impls le respectent.
- **I (Interface Segregation)** : petites interfaces ciblées (`CrudDao`, `ImmeubleService`).
- **D (Dependency Inversion)** : contrôleurs → services (interfaces) → DAO (interfaces). Impl injectées via constructeurs.

> À toi d'ajouter le reste des entités (Unite, Contrat, Paiement, Utilisateur) en copiant le modèle `Immeuble` + DAO/Service/Servlet correspondants.
