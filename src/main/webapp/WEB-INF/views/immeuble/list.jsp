<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Immeubles</title>

  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body { background: #f7f8fa; }
    .page-header {
      display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap;
      margin: 1.5rem 0 1rem;
    }
    .card-elevated {
      border: 0; border-radius: 1rem;
      box-shadow: 0 8px 24px rgba(0,0,0,.06);
    }
    .table thead th { background:#f0f2f5; font-weight:600; }
    .badge-city { background:#eef2ff; color:#3730a3; }
    .toolbar { display:flex; gap:.5rem; align-items:center; flex-wrap:wrap; }
    .search-input { max-width: 320px; }
    .empty-state {
      text-align:center; padding:3rem 1rem; color:#6b7280;
    }
  </style>
</head>
<body>

<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>

<div class="container">

  <!-- Header + actions -->
  <div class="page-header">
    <div class="d-flex align-items-center gap-2">
      <i class="bi bi-buildings fs-3 text-primary"></i>
      <div>
        <h3 class="m-0">Immeubles</h3>
        <small class="text-muted">Gestion et consultation des immeubles</small>
      </div>
    </div>

    <div class="toolbar">
      <input id="tableSearch" type="search" class="form-control search-input"
             placeholder="Rechercher: nom, adresse, ville…" aria-label="Rechercher">
      <c:set var="canEdit" value="${sessionScope.user != null && (sessionScope.user.role == 'ADMIN' || sessionScope.user.role == 'PROPRIETAIRE')}" />
      <c:if test="${canEdit}">
        <a href="${pageContext.request.contextPath}/app/immeubles?action=new" class="btn btn-primary">
          <i class="bi bi-plus-lg me-1"></i> Nouvel immeuble
        </a>
      </c:if>
    </div>
  </div>

  <!-- Card table -->
  <div class="card card-elevated">
    <div class="card-body p-0">

      <c:choose>
        <c:when test="${empty immeubles}">
          <div class="empty-state">
            <i class="bi bi-inbox fs-1 d-block mb-2"></i>
            <p class="mb-0">Aucun immeuble pour le moment.</p>
          </div>
        </c:when>

        <c:otherwise>
          <div class="table-responsive">
            <table id="immeublesTable" class="table align-middle mb-0">
              <thead>
                <tr>
                  <th style="width:80px">ID</th>
                  <th>Nom</th>
                  <th>Adresse</th>
                  <th style="width:180px">Ville</th>
                  <c:if test="${canEdit}"><th class="text-end" style="width:160px">Actions</th></c:if>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${immeubles}" var="im">
                  <tr>
                    <td class="text-muted">#${im.id}</td>
                    <td>
                      <div class="fw-semibold">${im.nom}</div>
                      <div class="text-muted small">Créé le
                        <span>
                          <c:out value="${im.createdAt}"/>
                        </span>
                      </div>
                    </td>
                    <td>
                      <div>${im.adresse}</div>
                      <c:if test="${not empty im.codePostal}">
                        <div class="text-muted small">CP : ${im.codePostal}</div>
                      </c:if>
                    </td>
                    <td>
                      <span class="badge rounded-pill badge-city">
                        <i class="bi bi-geo-alt me-1"></i>${im.ville}
                      </span>
                    </td>

                    <c:if test="${canEdit}">
                      <td class="text-end">
                        <div class="btn-group" role="group" aria-label="Actions">
                          <a href="${pageContext.request.contextPath}/app/immeubles?action=edit&id=${im.id}"
                             class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-pencil-square"></i>
                          </a>
                          <a href="${pageContext.request.contextPath}/app/immeubles?action=delete&id=${im.id}"
                             class="btn btn-sm btn-outline-danger"
                             onclick="return confirm('Supprimer cet immeuble ?');">
                            <i class="bi bi-trash"></i>
                          </a>
                        </div>
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
    const table = document.getElementById('immeublesTable');
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
