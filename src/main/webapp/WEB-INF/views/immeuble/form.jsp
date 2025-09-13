<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>${mode == 'edit' ? 'Modifier' : 'Nouvel'} immeuble</title>

  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body { background:#f7f8fa; }
    .page-wrap { max-width: 820px; margin: 24px auto; padding: 0 12px; }
    .card-elevated { border:0; border-radius: 1rem; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    .card-elevated .card-header { border-bottom:0; background:linear-gradient(180deg,#ffffff 0,#fafafa 100%); border-radius: 1rem 1rem 0 0; }
    .form-label.required::after { content:" *"; color:#dc3545; }
  </style>
</head>
<body>

<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>

<div class="page-wrap">
  <div class="d-flex align-items-center justify-content-between flex-wrap gap-2 mb-3">
    <div class="d-flex align-items-center gap-2">
      <i class="bi bi-building fs-3 text-primary"></i>
      <div>
        <h3 class="m-0">${mode == 'edit' ? 'Modifier' : 'Nouvel'} immeuble</h3>
        <small class="text-muted">Renseignez les informations de l’immeuble</small>
      </div>
    </div>
    <a href="${pageContext.request.contextPath}/app/immeubles" class="btn btn-outline-secondary">
      <i class="bi bi-arrow-left"></i> Retour à la liste
    </a>
  </div>

  <div class="card card-elevated">
    <div class="card-header">
      <div class="d-flex align-items-center gap-2">
        <i class="bi bi-pencil-square text-primary"></i>
        <strong>${mode == 'edit' ? 'Édition' : 'Création'}</strong>
      </div>
    </div>

    <div class="card-body">
      <!-- Message d'erreur (inchangé) -->
      <c:if test="${not empty error}">
        <div class="alert alert-danger d-flex align-items-center" role="alert">
          <i class="bi bi-exclamation-triangle-fill me-2"></i>
          <div><c:out value="${error}"/></div>
        </div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/app/immeubles" class="needs-validation" novalidate>
        <input type="hidden" name="action" value="${mode == 'edit' ? 'update' : 'create'}"/>
        <c:if test="${mode == 'edit'}">
          <input type="hidden" name="id" value="${immeuble.id}"/>
        </c:if>

        <div class="row g-3">
          <!-- Colonne 1 -->
          <div class="col-12 col-md-6">
            <label class="form-label required">Nom</label>
            <input class="form-control" name="nom" required value="<c:out value='${immeuble.nom}'/>"/>
            <div class="invalid-feedback">Le nom est requis.</div>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">Ville</label>
            <input class="form-control" name="ville" value="<c:out value='${immeuble.ville}'/>"/>
          </div>

          <div class="col-12">
            <label class="form-label">Adresse</label>
            <input class="form-control" name="adresse" value="<c:out value='${immeuble.adresse}'/>"/>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">Code Postal</label>
            <input class="form-control" name="codePostal" value="<c:out value='${immeuble.codePostal}'/>"/>
          </div>

          <div class="col-6 col-md-3">
            <label class="form-label">Année de construction</label>
            <input type="number" class="form-control" name="anneeConstruction" value="<c:out value='${immeuble.anneeConstruction}'/>"/>
          </div>

          <div class="col-6 col-md-3">
            <label class="form-label">Étages</label>
            <input type="number" class="form-control" name="nbEtages" value="<c:out value='${immeuble.nbEtages}'/>"/>
          </div>

          <div class="col-6 col-md-4">
            <label class="form-label">Nombre d'appartements</label>
            <input type="number" class="form-control" name="nbAppartements" value="<c:out value='${immeuble.nbAppartements}'/>"/>
          </div>

          <div class="col-6 col-md-4">
            <label class="form-label">Surface totale (m²)</label>
            <input type="number" step="0.01" class="form-control" name="surfaceTotale" value="<c:out value='${immeuble.surfaceTotale}'/>"/>
          </div>

          <div class="col-12">
            <label class="form-label">Description</label>
            <textarea class="form-control" rows="3" name="description"><c:out value='${immeuble.description}'/></textarea>
          </div>

          <!-- ADMIN : choix du propriétaire (inchangé côté data/EL) -->
          <c:if test="${sessionScope.user.role == 'ADMIN'}">
            <div class="col-12">
              <label class="form-label">Propriétaire</label>
              <select class="form-select" name="proprietaireId">
                <option value="">— Aucun —</option>
                <c:forEach items="${owners}" var="o">
                  <option value="${o.id}"
                    <c:if test="${not empty immeuble.proprietaire && immeuble.proprietaire.id == o.id}">selected</c:if>>
                    <c:out value="${o.username}"/> &lt;<c:out value="${o.email}"/>&gt;
                  </option>
                </c:forEach>
              </select>
              <div class="form-text">Option visible uniquement pour les administrateurs.</div>
            </div>
          </c:if>
        </div>

        <div class="d-flex gap-2 mt-4">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-check-circle me-1"></i> ${mode == 'edit' ? 'Enregistrer' : 'Créer'}
          </button>
          <a href="${pageContext.request.contextPath}/app/immeubles" class="btn btn-outline-secondary">
            <i class="bi bi-x-circle me-1"></i> Annuler
          </a>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Bootstrap bundle + validation légère -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  (() => {
    'use strict';
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
      form.addEventListener('submit', e => {
        if (!form.checkValidity()) {
          e.preventDefault(); e.stopPropagation();
        }
        form.classList.add('was-validated');
      }, false);
    });
  })();
</script>
</body>
</html>
