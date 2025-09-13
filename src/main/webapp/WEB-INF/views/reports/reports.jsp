<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Rapports</title>

  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body { background:#f6f8fb; }
    .page-wrap { padding: 16px; }
    .page-header {
      display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;
      margin: 12px 0 16px;
    }
    .card-elevated { border:0; border-radius: 1rem; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    .kpi {
      color:#0f172a; border:0; border-radius: 1rem; overflow: hidden;
    }
    .kpi .kpi-head { padding:12px 16px; color:#fff; }
    .kpi .kpi-body { padding:16px; background:#fff; }
    .kpi.encaisse .kpi-head { background: linear-gradient(135deg,#16a34a,#22c55e); }
    .kpi.attendu  .kpi-head { background: linear-gradient(135deg,#2563eb,#60a5fa); }
    .kpi.reste    .kpi-head { background: linear-gradient(135deg,#dc2626,#f87171); }
    .kpi .amount { font-size: 26px; font-weight: 700; }
    .table thead th { background:#f1f5f9; font-weight:600; }
    .badge-soft { border:1px solid transparent; }
    .badge-soft.success { background:#ecfdf5; color:#065f46; border-color:#a7f3d0; }
    .badge-soft.warning { background:#fffbeb; color:#92400e; border-color:#fde68a; }
    .badge-soft.danger  { background:#fef2f2; color:#991b1b; border-color:#fecaca; }
    .toolbar .form-select, .toolbar .form-control { min-width: 160px; }
    .export-btn { white-space: nowrap; }
  </style>
</head>
<body>

<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>

<div class="container page-wrap">
  <!-- Header -->
  <div class="page-header">
    <div class="d-flex align-items-center gap-2">
      <i class="bi bi-graph-up-arrow fs-3 text-primary"></i>
      <div>
        <h3 class="m-0">Rapports</h3>
        <small class="text-muted">Paiements, échéances et suivis</small>
      </div>
    </div>
  </div>

  <!-- Toolbar filtres + export -->
  <form method="get" action="${pageContext.request.contextPath}/app/reports" class="toolbar row g-2 align-items-end mb-3">
    <div class="col-12 col-sm-6 col-md-3">
      <label class="form-label">Du</label>
      <input type="date" name="from" value="${from}" class="form-control">
    </div>
    <div class="col-12 col-sm-6 col-md-3">
      <label class="form-label">Au</label>
      <input type="date" name="to" value="${to}" class="form-control">
    </div>
    <div class="col-12 col-sm-6 col-md-3">
      <label class="form-label">Statut</label>
      <select name="statut" class="form-select">
        <option value="" ${empty statut ? 'selected' : ''}>Tous</option>
        <option value="REGLE" ${statut == 'REGLE' ? 'selected' : ''}>Réglé</option>
        <option value="EN_ATTENTE" ${statut == 'EN_ATTENTE' ? 'selected' : ''}>En attente</option>
        <option value="EN_RETARD" ${statut == 'EN_RETARD' ? 'selected' : ''}>En retard</option>
      </select>
    </div>
    <div class="col-12 col-sm-6 col-md-3 d-flex gap-2">
      <button type="submit" class="btn btn-primary flex-grow-1">
        <i class="bi bi-funnel me-1"></i> Appliquer
      </button>
      <a class="btn btn-outline-secondary export-btn"
         href="${pageContext.request.contextPath}/app/reports?action=export&from=${from}&to=${to}&statut=${statut}">
        <i class="bi bi-download me-1"></i> Export CSV
      </a>
    </div>
  </form>

  <!-- KPIs -->
  <div class="row g-3 mb-3">
    <div class="col-12 col-md-4">
      <div class="kpi encaisse card-elevated">
        <div class="kpi-head d-flex align-items-center gap-2">
          <i class="bi bi-cash-coin"></i> <strong>Encaissements</strong>
        </div>
        <div class="kpi-body">
          <div class="amount"><c:out value="${encaisse}"/></div>
          <div class="text-muted small">Paiements reçus entre ${from} et ${to}</div>
        </div>
      </div>
    </div>
    <div class="col-12 col-md-4">
      <div class="kpi attendu card-elevated">
        <div class="kpi-head d-flex align-items-center gap-2">
          <i class="bi bi-journal-check"></i> <strong>Attendu</strong>
        </div>
        <div class="kpi-body">
          <div class="amount"><c:out value="${attendu}"/></div>
          <div class="text-muted small">Somme des échéances entre ${from} et ${to}</div>
        </div>
      </div>
    </div>
    <div class="col-12 col-md-4">
      <div class="kpi reste card-elevated">
        <div class="kpi-head d-flex align-items-center gap-2">
          <i class="bi bi-clipboard-x"></i> <strong>Reste dû</strong>
        </div>
        <div class="kpi-body">
          <div class="amount"><c:out value="${reste}"/></div>
          <div class="text-muted small">Attendu – Encaissements</div>
        </div>
      </div>
    </div>
  </div>

  <!-- Détail des paiements -->
  <div class="d-flex align-items-center justify-content-between flex-wrap gap-2 mb-2">
    <h5 class="m-0">Détail des paiements</h5>
    <input id="tableSearch" type="search" class="form-control" placeholder="Rechercher…" style="max-width: 280px;">
  </div>

  <c:choose>
    <c:when test="${empty lignes}">
      <div class="alert alert-info d-flex align-items-center" role="alert">
        <i class="bi bi-info-circle me-2"></i>
        <div>Aucune ligne sur la période sélectionnée.</div>
      </div>
    </c:when>
    <c:otherwise>
      <div class="card card-elevated">
        <div class="table-responsive">
          <table id="rapportsTable" class="table table-hover align-middle mb-0">
            <thead>
              <tr>
                <th>ID</th>
                <th>Échéance</th>
                <th>Montant</th>
                <th>Statut</th>
                <th>Date paiement</th>
                <th>Réf</th>
                <th>Contrat</th>
                <th>Locataire</th>
                <th>Email</th>
                <th>Immeuble</th>
                <th>Ville</th>
                <th>Appt</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${lignes}" var="p">
                <!-- mapping badge à partir du statut -->
                <c:set var="s" value="${fn:toLowerCase(p.statut)}"/>
                <c:set var="badgeClass"
                       value="${fn:contains(s,'regle') ? 'success' : (fn:contains(s,'retard') ? 'danger' : 'warning')}"/>

                <tr>
                  <td class="text-muted">#${p.id}</td>
                  <td><span class="text-nowrap">${p.echeance}</span></td>
                  <td><strong><c:out value="${p.montant}"/></strong></td>
                  <td>
                    <span class="badge badge-soft ${badgeClass}">
                      <c:out value="${p.statut}"/>
                    </span>
                  </td>
                  <td><span class="text-nowrap"><c:out value="${p.datePaiement}"/></span></td>
                  <td><c:out value="${p.reference}"/></td>
                  <td>${p.contrat.id}</td>
                  <td><c:out value="${p.contrat.locataire.nomComplet}"/></td>
                  <td><c:out value="${p.contrat.locataire.email}"/></td>
                  <td><c:out value="${p.contrat.appartement.immeuble.nom}"/></td>
                  <td><c:out value="${p.contrat.appartement.immeuble.ville}"/></td>
                  <td><c:out value="${p.contrat.appartement.numero}"/></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<!-- Bootstrap bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- Filtre client léger -->
<script>
  (function(){
    const input = document.getElementById('tableSearch');
    const table = document.getElementById('rapportsTable');
    if (!input || !table) return;

    input.addEventListener('input', function(){
      const q = this.value.toLowerCase().trim();
      const rows = table.tBodies[0]?.rows || [];
      for (const tr of rows) {
        const txt = tr.innerText.toLowerCase();
        tr.style.display = txt.includes(q) ? '' : 'none';
      }
    });
  })();
</script>
</body>
</html>
