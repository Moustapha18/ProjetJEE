<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="https://jakarta.ee/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Immeubles</title></head>
<body>
  <h2>Liste des immeubles</h2>
  <a href="${pageContext.request.contextPath}/app/immeubles/new">+ Ajouter</a>
  <table border="1" cellpadding="6">
    <thead><tr><th>ID</th><th>Nom</th><th>Adresse</th></tr></thead>
    <tbody>
      <c:forEach items="${items}" var="i">
        <tr>
          <td><c:out value="${i.id}"/></td>
          <td><c:out value="${i.nom}"/></td>
          <td><c:out value="${i.adresse}"/></td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</body>
</html>
