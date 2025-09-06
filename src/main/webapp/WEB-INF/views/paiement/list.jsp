<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Paiements</title>
  <style>
    .container{padding:14px}
    table { border-collapse: collapse }
    th, td { border:1px solid #ddd; padding:6px 10px }
  </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container">
  <h2>Liste des paiements</h2>
  <p><a href="${pageContext.request.contextPath}/app/paiements?action=new">➕ Nouveau paiement</a></p>

  <c:choose>
    <c:when test="${empty paiements}">
      <p>Aucun paiement pour le moment.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
        <tr>
          <th>ID</th><th>Contrat</th><th>Locataire</th><th>Appartement</th>
          <th>Date</th><th>Montant</th><th>Mode</th><th>Statut</th><th>Réf</th><th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${paiements}" var="p">
          <tr>
            <td>${p.id}</td>
            <td>#${p.contrat.id}</td>
            <td>${p.contrat.locataire.nomComplet}</td>
            <td>${p.contrat.appartement.numero} (${p.contrat.appartement.immeuble.nom})</td>
            <td>${p.datePaiement}</td>
            <td>${p.montant}</td>
            <td>${p.mode}</td>
            <td>${p.statut}</td>
            <td><c:out value="${p.reference}"/></td>
            <td>
              <a href="${pageContext.request.contextPath}/app/paiements?action=edit&id=${p.id}">✏️ Modifier</a> |
              <a href="${pageContext.request.contextPath}/app/paiements?action=delete&id=${p.id}"
                 onclick="return confirm('Supprimer ce paiement ?');">🗑️ Supprimer</a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
</div>
</body>
</html>
