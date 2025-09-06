<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Appartements</title>
  <style>
    .container{padding:14px}
    table { border-collapse: collapse }
    th, td { border: 1px solid #ddd; padding: 6px 10px }
  </style>
</head>
<body>

<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>


<div class="container">
  <h2>Liste des appartements</h2>

  <p><a href="${pageContext.request.contextPath}/app/appartements?action=new">➕ Nouvel appartement</a></p>

  <c:choose>
    <c:when test="${empty appartements}">
      <p>Aucun appartement pour le moment.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
        <tr>
          <th>ID</th><th>Numéro</th><th>Étage</th><th>Surface (m²)</th><th>Loyer</th><th>Immeuble</th><th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${appartements}" var="a">
          <tr>
            <td>${a.id}</td>
            <td>${a.numero}</td>
            <td><c:out value="${a.etage}"/></td>
            <td><c:out value="${a.surface}"/></td>
            <td><c:out value="${a.loyer}"/></td>
            <td>${a.immeuble.nom}</td>
            <td>
              <a href="${pageContext.request.contextPath}/app/appartements?action=edit&id=${a.id}">✏️ Modifier</a>
              &nbsp;|&nbsp;
              <a href="${pageContext.request.contextPath}/app/appartements?action=delete&id=${a.id}"
                 onclick="return confirm('Supprimer cet appartement ?');">🗑️ Supprimer</a>
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
