<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Appartements</title>


  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body { background-color: #f8f9fa; }
    .badge-dispo {
      background-color: #d1e7dd;
      color: #0f5132;
      font-weight: 500;
      font-size: .85rem;
    }
    .badge-occupe {
      background-color: #f8d7da;
      color: #842029;
      font-weight: 500;
      font-size: .85rem;
    }
    .filter-card {
      border-radius: .75rem;
      box-shadow: 0 4px 8px rgba(0,0,0,.05);
    }
    .table thead {
      background-color: #e9ecef;
    }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container my-4">
  <h2 class="mb-3"><i class="bi bi-door-open"></i> Liste des appartements</h2>

  <!-- Bouton création pour admin / propriétaire -->
  ${btnNouveauAppartement}


  <!-- Filtres -->
  <div class="card filter-card mb-4">
    <div class="card-body">
      <form class="row g-3 align-items-end" method="get" action="${pageContext.request.contextPath}/app/appartements">
        <div class="col-6 col-md-3">
          <label class="form-label">Prix min</label>
          <input type="number" step="0.01" class="form-control" name="min" value="${param.min}">
        </div>
        <div class="col-6 col-md-3">
          <label class="form-label">Prix max</label>
          <input type="number" step="0.01" class="form-control" name="max" value="${param.max}">
        </div>
        <div class="col-6 col-md-3">
          <label class="form-label">Ville</label>
          <input type="text" class="form-control" name="ville" value="${param.ville}">
        </div>
        <div class="col-6 col-md-3">
          <label class="form-label">Pièces</label>
          <input type="number" min="1" class="form-control" name="pieces" value="${param.pieces}">
        </div>
        <div class="col-12 d-flex justify-content-start gap-2">
          <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i> Rechercher</button>
          <a href="${pageContext.request.contextPath}/app/appartements" class="btn btn-outline-secondary">Réinitialiser</a>
        </div>
      </form>
    </div>
  </div>

  <!-- Table des appartements -->
  <div class="card shadow-sm">
    <div class="card-body p-0">
      <table class="table table-hover align-middle mb-0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Numéro</th>
            <th>Étage</th>
            <th>Surface (m²)</th>
            <th>Pièces</th>
            <th>Loyer</th>
            <th>Immeuble</th>
            <th>Ville</th>
            <th>Disponibilité</th>
            <th class="text-end">Actions</th>
          </tr>
        </thead>
        <tbody>
          ${tableAppartements}
        </tbody>
      </table>
    </div>
  </div>
</div>

<!-- JS Bootstrap -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
