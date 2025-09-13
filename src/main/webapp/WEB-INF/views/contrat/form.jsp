<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>
    <c:choose><c:when test="${mode eq 'edit'}">Modifier</c:when><c:otherwise>Nouveau</c:otherwise></c:choose>
    contrat
  </title>

  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body { background:#f7f8fa; }
    .page-wrap { max-width: 880px; margin: 24px auto; padding: 0 12px; }
    .card-elevated { border:0; border-radius: 1rem; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    .card-elevated .card-header { border-bottom:0; background:linear-gradient(180deg,#ffffff 0,#fafafa 100%); border-radius: 1rem 1rem 0 0; }
    .form-label.required::after { content:" *"; color:#dc3545; }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="page-wrap">

  <div class="d-flex align-items-center justify-content-between flex-wrap gap-2 mb-3">
    <div class="d-flex align-items-center gap-2">
      <i class="bi bi-file-earmark-text fs-3 text-primary"></i>
      <div>
        <h3 class="m-0">
          <c:choose><c:when test="${mode eq 'edit'}">Modifier</c:when><c:otherwise>Nouveau</c:otherwise></c:choose>
          contrat
        </h3>
        <small class="text-muted">Renseignez les informations du bail</small>
      </div>
    </div>
    <a href="${pageContext.request.contextPath}/app/contrats" class="btn btn-outline-secondary">
      <i class="bi bi-arrow-left"></i> Retour à la liste
    </a>
  </div>

  <div class="card card-elevated">
    <div class="card-header">
      <div class="d-flex align-items-center gap-2">
        <i class="bi bi-pencil-square text-primary"></i>
        <strong><c:out value="${mode eq 'edit' ? 'Édition' : 'Création'}"/></strong>
      </div>
    </div>

    <div class="card-body">

      <!-- Erreur serveur (inchangée) -->
      <c:if test="${not empty error}">
        <div class="alert alert-danger d-flex align-items-center" role="alert">
          <i class="bi bi-exclamation-triangle-fill me-2"></i>
          <div>${error}</div>
        </div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/app/contrats" class="needs-validation" novalidate>
        <c:choose>
          <c:when test="${mode eq 'edit' && not empty contrat}">
            <input type="hidden" name="id" value="${contrat.id}" />
            <input type="hidden" name="action" value="update" />
          </c:when>
          <c:otherwise><input type="hidden" name="action" value="create" /></c:otherwise>
        </c:choose>

        <div class="row g-3">
          <!-- Appartement -->
          <div class="col-12 col-md-6">
            <label class="form-label required">Appartement</label>
            <select name="appartementId" class="form-select" required>
              <option value="">-- choisir --</option>
              <c:forEach items="${appartements}" var="a">
                <option value="${a.id}" <c:if test="${not empty contrat && contrat.appartement.id == a.id}">selected</c:if>>
                  ${a.numero} - ${a.immeuble.nom}
                </option>
              </c:forEach>
            </select>
            <div class="invalid-feedback">Sélectionnez un appartement.</div>
          </div>

          <!-- Locataire -->
          <div class="col-12 col-md-6">
            <label class="form-label required">Locataire</label>
            <select name="locataireId" class="form-select" required>
              <option value="">-- choisir --</option>
              <c:forEach items="${locataires}" var="l">
                <option value="${l.id}" <c:if test="${not empty contrat && contrat.locataire.id == l.id}">selected</c:if>>
                  ${l.nomComplet}
                </option>
              </c:forEach>
            </select>
            <div class="invalid-feedback">Sélectionnez un locataire.</div>
          </div>

          <!-- Dates -->
          <div class="col-12 col-md-6">
            <label class="form-label required">Date début</label>
            <input type="date" name="dateDebut" class="form-control" value="${empty contrat ? '' : contrat.dateDebut}" required />
            <div class="invalid-feedback">La date de début est requise.</div>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">Date fin (optionnel)</label>
            <input type="date" name="dateFin" class="form-control" value="${empty contrat ? '' : contrat.dateFin}" />
          </div>

          <!-- Montants -->
          <div class="col-12 col-md-6">
            <label class="form-label required">Loyer mensuel (FCFA)</label>
            <input type="number" step="0.01" name="loyerMensuel" class="form-control" value="${empty contrat ? '' : contrat.loyerMensuel}" required />
            <div class="invalid-feedback">Le loyer mensuel est requis.</div>
          </div>

          <div class="col-12 col-md-6">
            <label class="form-label">Caution (FCFA)</label>
            <input type="number" step="0.01" name="caution" class="form-control" value="${empty contrat ? '' : contrat.caution}" />
          </div>

          <!-- Statut -->
          <div class="col-12 col-md-6">
            <label class="form-label required">Statut</label>
            <select name="statut" class="form-select" required>
              <c:forEach items="${statuts}" var="s">
                <option value="${s}" <c:if test="${not empty contrat && contrat.statut == s}">selected</c:if>>${s}</option>
              </c:forEach>
            </select>
            <div class="invalid-feedback">Le statut est requis.</div>
          </div>
        </div>

        <div class="d-flex gap-2 mt-4">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-check-circle me-1"></i> Enregistrer
          </button>
          <a href="${pageContext.request.contextPath}/app/contrats" class="btn btn-outline-secondary">
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
