<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Contrats</title>
  <style>
    .container { padding:14px }
    table { border-collapse: collapse; width: 100% }
    th, td { border:1px solid #ddd; padding:6px 10px; text-align:left }
  </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container">
  <h2>Liste des contrats</h2>
  <p><a href="${pageContext.request.contextPath}/app/contrats?action=new">➕ Nouveau contrat</a></p>

  <c:choose>
    <c:when test="${empty contrats}">
      <p>Aucun contrat pour le moment.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>Appartement</th>
          <th>Locataire</th>
          <th>Début</th>
          <th>Fin</th>
          <th>Loyer</th>
          <th>Statut</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${contrats}" var="c">
          <tr>
            <td>${c.id}</td>
           <td>
             <c:choose>
               <c:when test="${not empty c.appartement}">
                 ${c.appartement.numero}
                 <c:if test="${not empty c.appartement.immeuble}">
                   (${c.appartement.immeuble.nom})
                 </c:if>
               </c:when>
               <c:otherwise>-</c:otherwise>
             </c:choose>
           </td>

           <td>
             <c:choose>
               <c:when test="${not empty c.locataire}">
                 ${c.locataire.nomComplet}
               </c:when>
               <c:otherwise>-</c:otherwise>
             </c:choose>
           </td>

            <td>${c.dateDebut}</td>
            <td><c:out value="${c.dateFin}"/></td>
            <td>${c.loyerMensuel}</td>
            <td>${c.statut}</td>
            <td>
              <a href="${pageContext.request.contextPath}/app/contrats?action=edit&id=${c.id}">✏️ Modifier</a> |
              <a href="${pageContext.request.contextPath}/app/contrats?action=delete&id=${c.id}"
                 onclick="return confirm('Supprimer ce contrat ?');">🗑️ Supprimer</a>
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
