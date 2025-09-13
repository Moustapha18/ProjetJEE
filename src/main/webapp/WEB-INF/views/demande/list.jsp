<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Demandes de location</title>

  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body { background:#f7f7fb }
    .page-title { display:flex; align-items:center; gap:.5rem; margin:20px 0 10px }
    .page-title i { font-size:1.2rem }
    .card-elevated { border-radius:14px; box-shadow:0 6px 18px rgba(0,0,0,.06) }
    .badge-soft { border:1px solid transparent; font-weight:600; font-size:.8rem; padding:.3rem .6rem; border-radius:999px }
    .b-wait { background:#fff7ed; color:#92400e; border-color:#fde68a }  /* EN_ATTENTE */
    .b-ok   { background:#ecfdf5; color:#065f46; border-color:#a7f3d0 }  /* ACCEPTEE */
    .b-ko   { background:#fef2f2; color:#991b1b; border-color:#fecaca }  /* REJETEE */
    .muted { color:#6b7280 }
    .cell-sm { font-size:.92rem }
    .text-truncate-2 {
      display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;
      max-width:340px;
    }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container py-3">

  <!-- Titre -->
  <div class="page-title">
    <i class="bi bi-bookmark-check"></i>
    <h3 class="mb-0">Demandes de location</h3>
  </div>
  <p class="text-muted mb-3">Gestion des réservations d’appartements (créées par les locataires, modérées par propriétaire ou admin).</p>

  <!-- Alerte d'erreur si besoin -->
  <c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}"/></div>
  </c:if>

  <!-- Rôle -->
  <c:set var="role" value="${sessionScope.user != null ? sessionScope.user.role : ''}" />

  <!-- Tableau -->
  <div class="card card-elevated">
    <div class="card-body p-0">
      <c:choose>
        <c:when test="${empty demandes}">
          <div class="p-4 text-center text-muted">Aucune demande.</div>
        </c:when>
        <c:otherwise>
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
              <tr>
                <th class="text-muted">#</th>
                <th>Appartement</th>
                <th>Immeuble</th>
                <th>Ville</th>
                <th>Locataire</th>
                <th>Message</th>
                <th>Statut</th>
                <th>Créée le</th>
                <th class="text-end">Actions</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${demandes}" var="d">
                <tr>
                  <!-- ID -->
                  <td class="text-muted">#${d.id}</td>

                  <!-- Appartement -->
                  <td class="cell-sm">
                    <c:choose>
                      <c:when test="${not empty d.appartement}">
                        <div class="fw-semibold">Appt <c:out value="${d.appartement.numero}"/></div>
                        <div class="small muted">ID: <c:out value="${d.appartement.id}"/></div>
                      </c:when>
                      <c:otherwise>-</c:otherwise>
                    </c:choose>
                  </td>

                  <!-- Immeuble -->
                  <td class="cell-sm">
                    <c:choose>
                      <c:when test="${not empty d.appartement and not empty d.appartement.immeuble}">
                        <div class="fw-semibold"><c:out value="${d.appartement.immeuble.nom}"/></div>
                        <div class="small muted">ID: <c:out value="${d.appartement.immeuble.id}"/></div>
                      </c:when>
                      <c:otherwise>-</c:otherwise>
                    </c:choose>
                  </td>

                  <!-- Ville -->
                  <td class="cell-sm">
                    <c:choose>
                      <c:when test="${not empty d.appartement and not empty d.appartement.immeuble}">
                        <c:out value="${d.appartement.immeuble.ville}"/>
                      </c:when>
                      <c:otherwise>-</c:otherwise>
                    </c:choose>
                  </td>

                  <!-- Locataire -->
                  <td class="cell-sm">
                    <c:choose>
                      <c:when test="${not empty d.locataire}">
                        <div class="fw-semibold"><c:out value="${d.locataire.username}"/></div>
                        <div class="small muted"><c:out value="${d.locataire.email}"/></div>
                      </c:when>
                      <c:otherwise>-</c:otherwise>
                    </c:choose>
                  </td>

                  <!-- Message -->
                  <td class="cell-sm">
                    <div class="text-truncate-2" title="${d.message}">
                      <c:out value="${d.message}"/>
                    </div>
                  </td>

                  <!-- Statut -->
                  <td>
                    <c:choose>
                      <c:when test="${d.statut == 'EN_ATTENTE'}"><span class="badge-soft b-wait">En attente</span></c:when>
                      <c:when test="${d.statut == 'ACCEPTEE'}"><span class="badge-soft b-ok">Acceptée</span></c:when>
                      <c:when test="${d.statut == 'REJETEE'}"><span class="badge-soft b-ko">Rejetée</span></c:when>
                      <c:otherwise><span class="badge-soft">${d.statut}</span></c:otherwise>
                    </c:choose>
                  </td>

                  <!-- Créée le -->
                  <td class="small text-muted">
                    <c:out value="${d.createdAt}"/>
                  </td>

                  <!-- Actions -->
                  <td class="text-end">
                    <div class="d-inline-flex gap-2">
                      <c:if test="${(role == 'ADMIN' || role == 'PROPRIETAIRE') && d.statut == 'EN_ATTENTE'}">
                        <form class="d-inline" method="post" action="${pageContext.request.contextPath}/app/demandes">
                          <input type="hidden" name="action" value="approve"/>
                          <input type="hidden" name="id" value="${d.id}"/>
                          <button type="submit" class="btn btn-success btn-sm">
                            <i class="bi bi-check2"></i> Valider
                          </button>
                        </form>
                        <form class="d-inline" method="post" action="${pageContext.request.contextPath}/app/demandes"
                              onsubmit="return confirm('Refuser cette demande ?');">
                          <input type="hidden" name="action" value="reject"/>
                          <input type="hidden" name="id" value="${d.id}"/>
                          <button type="submit" class="btn btn-outline-danger btn-sm">
                            <i class="bi bi-x"></i> Refuser
                          </button>
                        </form>
                      </c:if>

                      <c:if test="${role == 'LOCATAIRE' && d.statut == 'ACCEPTEE'}">
                        <span class="badge-soft b-ok">Contrat en cours</span>
                      </c:if>

                      <c:if test="${!( (role == 'ADMIN' || role == 'PROPRIETAIRE') && d.statut == 'EN_ATTENTE') && !(role == 'LOCATAIRE' && d.statut == 'ACCEPTEE')}">
                        <span class="text-muted small">—</span>
                      </c:if>
                    </div>
                  </td>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
