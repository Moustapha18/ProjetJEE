<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Contrats</title>

  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body { background:#f7f8fa; }
    .page-wrap { padding: 16px; }
    .page-header {
      display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;
      margin: 12px 0 16px;
    }
    .card-elevated { border:0; border-radius: 1rem; box-shadow: 0 8px 24px rgba(0,0,0,.06); }
    .table thead th { background:#f0f2f5; font-weight:600; }
    .warn { color:#b45309; font-size:14px; margin-left:6px }
    .badge-soft { border:1px solid transparent; }
    .badge-soft.success { background:#ecfdf5; color:#065f46; border-color:#a7f3d0; }
    .badge-soft.danger  { background:#fef2f2; color:#991b1b; border-color:#fecaca; }
    .badge-soft.secondary{ background:#f1f5f9; color:#0f172a; border-color:#e2e8f0; }
    .search-input { max-width: 320px; }
    .actions form { display:inline-flex; gap:6px; align-items:center; margin:2px 4px; flex-wrap:wrap; }
    .actions input[type="date"], .actions input[type="number"] { padding:.25rem .5rem; font-size:.875rem; }
    .empty-state { text-align:center; padding:3rem 1rem; color:#6b7280; }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container page-wrap">
  <!-- Rôle & droits (inchangé) -->
  <c:set var="role" value="${sessionScope.user != null ? sessionScope.user.role : ''}" />
  <c:set var="canEdit" value="${role == 'ADMIN' || role == 'PROPRIETAIRE'}" />

  <!-- Header -->
  <div class="page-header">
    <div class="d-flex align-items-center gap-2">
      <i class="bi bi-file-earmark-text fs-3 text-primary"></i>
      <div>
        <h3 class="m-0">Contrats</h3>
        <small class="text-muted">Suivi des baux, résiliations et renouvellements</small>
      </div>
    </div>
    <div class="d-flex align-items-center gap-2">
      <input id="tableSearch" type="search" class="form-control search-input" placeholder="Rechercher: locataire, immeuble…" aria-label="Rechercher">
    </div>
  </div>

  <div class="card card-elevated">
    <div class="card-body p-0">
      <c:choose>
        <c:when test="${empty contrats}">
          <div class="empty-state">
            <i class="bi bi-inbox fs-1 d-block mb-2"></i>
            <p class="mb-0">Aucun contrat pour le moment.</p>
          </div>
        </c:when>

        <c:otherwise>
          <div class="table-responsive">
            <table id="contratsTable" class="table align-middle mb-0">
              <thead>
              <tr>
                <th style="width:80px">ID</th>
                <th>Appartement</th>
                <th>Locataire</th>
                <th>Début</th>
                <th>Fin</th>
                <th>Loyer</th>
                <th>Statut</th>
                <c:if test="${canEdit}"><th class="text-end" style="min-width:360px">Actions</th></c:if>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${contrats}" var="c">
                <!-- Pré-calculs statut (inchangés fonctionnellement) -->
                <c:set var="statStr" value="${c.statut != null ? c.statut : ''}"/>
                <c:set var="statLower" value="${fn:toLowerCase(fn:replace(statStr,'_',''))}"/>
                <c:set var="isActive" value="${fn:contains(statLower,'actif') or fn:contains(statLower,'encours')}"/>
                <c:set var="badgeClass" value="${isActive ? 'success' : (fn:contains(statLower,'resil') or fn:contains(statLower,'inactif') or fn:contains(statLower,'term') ? 'danger' : 'secondary')}"/>

                <tr>
                  <td class="text-muted">#<c:out value="${c.id}"/></td>

                  <td>
                    <c:choose>
                      <c:when test="${not empty c.appartement}">
                        <div class="fw-semibold">
                          <c:out value="${c.appartement.numero}"/>
                        </div>
                        <c:if test="${not empty c.appartement.immeuble}">
                          <div class="text-muted small">
                            <i class="bi bi-buildings me-1"></i>
                            <c:out value="${c.appartement.immeuble.nom}"/>
                          </div>
                        </c:if>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">—</span>
                      </c:otherwise>
                    </c:choose>
                  </td>

                  <td>
                    <c:choose>
                      <c:when test="${not empty c.locataire}">
                        <div class="fw-semibold"><c:out value="${c.locataire.nomComplet}"/></div>
                      </c:when>
                      <c:otherwise><span class="text-muted">—</span></c:otherwise>
                    </c:choose>
                  </td>

                  <td><span class="text-nowrap"><c:out value="${c.dateDebut}"/></span></td>
                  <td><span class="text-nowrap"><c:out value="${c.dateFin}"/></span></td>
                  <td><span class="text-nowrap"><c:out value="${c.loyerMensuel}"/></span></td>

                  <td>
                    <span class="badge badge-soft ${badgeClass}">
                      <c:out value="${c.statut}"/>
                    </span>
                    <!-- Avertissements de cohérence (inchangés) -->
                    <c:if test="${c.dateFin == null and not isActive}">
                      <span class="warn" title="Statut non actif mais pas de date de fin">⚠</span>
                    </c:if>
                    <c:if test="${c.dateFin != null and isActive}">
                      <span class="warn" title="Actif mais une date de fin est définie">⚠</span>
                    </c:if>
                  </td>

                  <c:if test="${canEdit}">
                    <td class="text-end actions">
                      <!-- Résilier -->
                      <form method="post" action="${pageContext.request.contextPath}/app/contrats" class="mb-1">
                        <input type="hidden" name="action" value="terminate"/>
                        <input type="hidden" name="id" value="${c.id}"/>
                        <input type="date" name="dateFin" class="form-control form-control-sm" />
                        <button type="submit" class="btn btn-sm btn-outline-danger">
                          <i class="bi bi-x-octagon me-1"></i> Résilier
                        </button>
                      </form>

                      <!-- Renouveler + échéancier -->
                      <form method="post" action="${pageContext.request.contextPath}/app/contrats" class="mb-1">
                        <input type="hidden" name="action" value="renew"/>
                        <input type="hidden" name="id" value="${c.id}"/>
                        <input type="date" name="dateDebut" class="form-control form-control-sm" placeholder="Date début"/>
                        <input type="number" step="0.01" name="loyer" class="form-control form-control-sm" placeholder="Nouveau loyer (opt.)"/>
                        <button type="submit" class="btn btn-sm btn-outline-primary">
                          <i class="bi bi-arrow-repeat me-1"></i> Renouveler + échéancier
                        </button>
                      </form>

                      <!-- Générer un échéancier personnalisé -->
                      <form method="post" action="${pageContext.request.contextPath}/app/contrats">
                        <input type="hidden" name="action" value="gensched"/>
                        <input type="hidden" name="id" value="${c.id}"/>
                        <input type="date" name="from" class="form-control form-control-sm" placeholder="Du (1er du mois)"/>
                        <input type="date" name="to" class="form-control form-control-sm" placeholder="Au (fin de mois)"/>
                        <button type="submit" class="btn btn-sm btn-outline-secondary">
                          <i class="bi bi-calendar-check me-1"></i> Générer échéancier
                        </button>
                      </form>
                    </td>
                  </c:if>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>

<!-- Bootstrap bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- Filtre client léger -->
<script>
  (function(){
    const input = document.getElementById('tableSearch');
    const table = document.getElementById('contratsTable');
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
