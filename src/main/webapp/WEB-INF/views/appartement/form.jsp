<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>${pageTitle}</title>

  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body { background-color: #f8f9fa; }
    .form-card {
      max-width: 600px;
      margin: 2rem auto;
      background: #fff;
      border-radius: 1rem;
      box-shadow: 0 6px 18px rgba(0,0,0,.08);
      padding: 1.5rem;
    }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container">
  <div class="form-card">
    <h3 class="mb-3">
      <i class="bi bi-building"></i>
      ${formTitle}
    </h3>

    ${errorMsg}

    <form method="post" action="${pageContext.request.contextPath}/app/appartements" class="needs-validation" novalidate>

      ${hiddenFields}

      <div class="mb-3">
        <label class="form-label">Numéro</label>
        <input type="text" class="form-control" name="numero" value="${numero}" required>
      </div>

      <div class="mb-3">
        <label class="form-label">Étage</label>
        <input type="number" class="form-control" name="etage" value="${etage}">
      </div>

      <div class="mb-3">
        <label class="form-label">Surface (m²)</label>
        <input type="number" step="0.01" class="form-control" name="surface" value="${surface}">
      </div>

      <div class="mb-3">
        <label class="form-label">Nombre de pièces</label>
        <input type="number" min="1" class="form-control" name="nbPieces" value="${nbPieces}" required>
      </div>

      <div class="mb-3">
        <label class="form-label">Loyer (FCFA)</label>
        <input type="number" step="0.01" class="form-control" name="loyer" value="${loyer}">
      </div>

      <div class="mb-3">
        <label class="form-label">Immeuble</label>
        <select class="form-select" name="immeubleId" required>
          <option value="">-- choisir --</option>
          ${optionsImmeubles}
        </select>
      </div>

      <div class="d-flex gap-3">
        <button type="submit" class="btn btn-primary">
          <i class="bi bi-check-circle"></i> Enregistrer
        </button>
        <a href="${pageContext.request.contextPath}/app/appartements" class="btn btn-outline-secondary">
          <i class="bi bi-x-circle"></i> Annuler
        </a>
      </div>
    </form>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  (() => {
    'use strict'
    const forms = document.querySelectorAll('.needs-validation')
    Array.from(forms).forEach(form => {
      form.addEventListener('submit', event => {
        if (!form.checkValidity()) {
          event.preventDefault()
          event.stopPropagation()
        }
        form.classList.add('was-validated')
      }, false)
    })
  })()
</script>

</body>
</html>
