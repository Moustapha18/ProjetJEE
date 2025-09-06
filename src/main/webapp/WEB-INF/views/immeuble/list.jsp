<!-- src/main/webapp/WEB-INF/views/immeuble/list.jsp -->
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Immeubles</title>
  <style>
    .container{padding:14px}
    table { border-collapse: collapse; width:100% }
    th, td { border: 1px solid #ddd; padding: 6px 10px; vertical-align: top }
  </style>
</head>
<body>

<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>

<div class="container">
  <h2>Liste des immeubles</h2>

  <p><a href="${pageContext.request.contextPath}/app/immeubles?action=new">➕ Nouvel immeuble</a></p>

  <c:choose>
    <c:when test="${empty immeubles}">
      <p>Aucun immeuble pour le moment.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr>
            <th>ID</th><th>Nom</th><th>Adresse</th><th>Ville</th><th>CP</th>
            <th>Année</th><th>Étages</th><th>Apparts</th><th>Surface (m²)</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${immeubles}" var="im">
            <tr>
              <td>${im.id}</td>
              <td>${im.nom}</td>
              <td>${im.adresse}</td>
              <td>${im.ville}</td>
              <td><c:out value="${im.codePostal}"/></td>
              <td><c:out value="${im.anneeConstruction}"/></td>
              <td><c:out value="${im.nbEtages}"/></td>
              <td><c:out value="${im.nbAppartements}"/></td>
              <td><c:out value="${im.surfaceTotale}"/></td>
              <td>
                <a href="${pageContext.request.contextPath}/app/immeubles?action=edit&id=${im.id}">✏️ Modifier</a>
                &nbsp;|&nbsp;
                <a href="${pageContext.request.contextPath}/app/immeubles?action=delete&id=${im.id}"
                   onclick="return confirm('Supprimer cet immeuble ?');">🗑️ Supprimer</a>
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
