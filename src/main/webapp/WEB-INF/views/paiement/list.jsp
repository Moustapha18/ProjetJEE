<!-- src/main/webapp/WEB-INF/views/paiement/list.jsp -->
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Paiements</title>

  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
  <style>
    body { background:#f7f7fb }
    .page-title { display:flex; align-items:center; gap:.5rem; margin:20px 0 10px }
    .card-elevated { border-radius:14px; box-shadow:0 6px 18px rgba(0,0,0,.06) }
    .badge-soft{ border:1px solid transparent; font-weight:600; font-size:.8rem; padding:.3rem .6rem; border-radius:999px }
    .b-wait { background:#fff7ed; color:#92400e; border-color:#fde68a }  /* EN_ATTENTE */
    .b-late { background:#fff1f2; color:#991b1b; border-color:#fecaca }  /* RETARD */
    .b-ok   { background:#ecfdf5; color:#065f46; border-color:#a7f3d0 }  /* PAYE */
    .ref-input{ width:180px }
  </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container py-3">
  <div class="page-title">
    <i class="bi bi-cash-coin"></i>
    <h3 class="mb-0">Paiements</h3>
  </div>

  <c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}"/></div>
  </c:if>

  <div class="card card-elevated">
    <div class="card-body p-0">
      <c:choose>
        <c:when test="${empty paiements}">
          <div class="p-4 text-center text-muted">Aucune ligne.</div>
        </c:when>
        <c:otherwise>
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
              <tr>
                <th class="text-muted">#</th>
                <th>Échéance</th>
                <th>Montant</th>
                <th>Statut</th>
                <th>Date paiement</th>
                <th>Référence</th>
                <th>Contrat</th>
                <th>Locataire</th>
                <th>Immeuble</th>
                <th>Ville</th>
                <th>Appt</th>
                <th class="text-end">Action</th>
              </tr>
              </thead>
              <tbody>
              <c:set var="role" value="${sessionScope.user != null ? sessionScope.user.role : ''}" />
              <c:forEach items="${paiements}" var="p">
                <tr>
                  <td class="text-muted">#${p.id}</td>
                  <td>${p.echeance}</td>
                  <td>${p.montant}</td>
                  <td>
                    <c:choose>
                      <c:when test="${p.statut == 'PAYE'}"><span class="badge-soft b-ok">Payé</span></c:when>
                      <c:when test="${p.statut == 'RETARD'}"><span class="badge-soft b-late">En retard</span></c:when>
                      <c:otherwise><span class="badge-soft b-wait">En attente</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td><c:out value="${p.datePaiement}"/></td>
                  <td><c:out value="${p.reference}"/></td>
                  <td>${p.contrat.id}</td>
                  <td><c:out value="${p.contrat.locataire != null ? p.contrat.locataire.nomComplet : ''}"/></td>
                  <td><c:out value="${p.contrat.appartement != null && p.contrat.appartement.immeuble != null ? p.contrat.appartement.immeuble.nom : ''}"/></td>
                  <td><c:out value="${p.contrat.appartement != null && p.contrat.appartement.immeuble != null ? p.contrat.appartement.immeuble.ville : ''}"/></td>
                  <td><c:out value="${p.contrat.appartement != null ? p.contrat.appartement.numero : ''}"/></td>

                  <td class="text-end">
                    <!-- LOCATAIRE : bouton Payer si non payé -->
                    <c:if test="${role == 'LOCATAIRE' && p.statut != 'PAYE'}">
                      <form class="d-inline" method="post" action="${pageContext.request.contextPath}/app/paiements">
                        <input type="hidden" name="action" value="pay"/>
                        <input type="hidden" name="id" value="${p.id}"/>
                        <input type="text" name="reference" class="form-control d-inline ref-input me-1" placeholder="Référence (optionnel)"/>
                        <button type="submit" class="btn btn-success btn-sm">
                          <i class="bi bi-credit-card"></i> Payer
                        </button>
                      </form>
                    </c:if>

                    <!-- ADMIN/PROPRIO : marquer payé (date libre) -->
                    <c:if test="${(role == 'ADMIN' || role == 'PROPRIETAIRE') && p.statut != 'PAYE'}">
                      <form class="d-inline" method="post" action="${pageContext.request.contextPath}/app/paiements">
                        <input type="hidden" name="action" value="markPaid"/>
                        <input type="hidden" name="id" value="${p.id}"/>
                        <input type="date" name="datePaiement" class="form-control d-inline me-1" style="width:160px"/>
                        <input type="text"  name="reference" class="form-control d-inline me-1" style="width:160px" placeholder="Référence"/>
                        <button type="submit" class="btn btn-primary btn-sm">
                          <i class="bi bi-check2-square"></i> Valider
                        </button>
                      </form>
                    </c:if>

                    <c:if test="${p.statut == 'PAYE'}">
                      <a class="btn btn-outline-secondary btn-sm"
                         href="${pageContext.request.contextPath}/app/paiements?action=recu&id=${p.id}">
                        <i class="bi bi-printer"></i> Reçu
                      </a>
                    </c:if>

                    <c:if test="${p.statut != 'PAYE' && !(role == 'LOCATAIRE' || role == 'ADMIN' || role == 'PROPRIETAIRE')}">
                      <span class="text-muted small">—</span>
                    </c:if>
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
