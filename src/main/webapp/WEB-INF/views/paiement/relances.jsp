<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Relances paiements en retard</title>
  <style>
    .container{padding:14px}
    table{border-collapse:collapse;width:100%}
    th,td{border:1px solid #ddd;padding:6px 10px;vertical-align:top}
    .ok{color:#065f46}
    .muted{color:#64748b}
    form.inline{display:inline-block;margin:0}
  </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>
<div class="container">
  <h2>Relances – paiements en retard</h2>

  <c:if test="${param.ok == '1'}"><p class="ok">Relances envoyées ✅</p></c:if>

  <c:choose>
    <c:when test="${empty late}">
      <p class="muted">Aucun paiement en retard ✨</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Locataire</th>
            <th>Appartement</th>
            <th>Échéance</th>
            <th>Montant</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach items="${late}" var="p">
          <tr>
            <td>${p.id}</td>
            <td>${p.contrat.locataire.email}</td>
            <td>${p.contrat.appartement.numero} — ${p.contrat.appartement.immeuble.nom}</td>
            <td>${p.echeance}</td>
            <td>${p.montant}</td>
            <td>
              <form class="inline" method="post" action="${pageContext.request.contextPath}/app/relances">
                <input type="hidden" name="action" value="relancer"/>
                <input type="hidden" name="id" value="${p.id}"/>
                <button type="submit">Relancer</button>
              </form>
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
