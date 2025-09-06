<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Locataires</title>
  <style>
    .container{padding:14px}
    table { border-collapse: collapse }
    th, td { border: 1px solid #ddd; padding: 6px 10px }
  </style>
</head>
<body>
<div class="container">
  <h2>Liste des locataires</h2>

  <p><a href="${pageContext.request.contextPath}/app/locataires?action=new">➕ Nouveau locataire</a></p>

  <c:choose>
    <c:when test="${empty locataires}">
      <p>Aucun locataire pour le moment.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
        <tr><th>ID</th><th>Nom complet</th><th>Email</th><th>Téléphone</th><th>Actions</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${locataires}" var="l">
          <tr>
            <td>${l.id}</td>
            <td>${l.nomComplet}</td>
            <td>${l.email}</td>
            <td>${l.telephone}</td>
            <td>
              <a href="${pageContext.request.contextPath}/app/locataires?action=edit&id=${l.id}">✏️ Modifier</a>
              &nbsp;|&nbsp;
              <a href="${pageContext.request.contextPath}/app/locataires?action=delete&id=${l.id}"
                 onclick="return confirm('Supprimer ce locataire ?');">🗑️ Supprimer</a>
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
