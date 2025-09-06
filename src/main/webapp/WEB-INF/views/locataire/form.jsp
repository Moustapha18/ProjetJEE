<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>
    <c:choose>
      <c:when test="${mode eq 'edit'}">Modifier</c:when>
      <c:otherwise>Créer</c:otherwise>
    </c:choose>
    un locataire
  </title>
  <style>.container{padding:14px}</style>
</head>
<body>
<div class="container">
  <h2>
    <c:choose>
      <c:when test="${mode eq 'edit'}">Modifier</c:when>
      <c:otherwise>Créer</c:otherwise>
    </c:choose>
    un locataire
  </h2>

  <c:if test="${not empty error}">
    <div style="color:red">${error}</div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/app/locataires">
    <c:choose>
      <c:when test="${mode eq 'edit' && not empty locataire}">
        <input type="hidden" name="id" value="${locataire.id}" />
        <input type="hidden" name="action" value="update" />
      </c:when>
      <c:otherwise>
        <input type="hidden" name="action" value="create" />
      </c:otherwise>
    </c:choose>

    <div>
      <label>Nom complet</label><br/>
      <input type="text" name="nomComplet" value="${empty locataire ? '' : locataire.nomComplet}" required />
    </div>
    <br/>
    <div>
      <label>Email</label><br/>
      <input type="email" name="email" value="${empty locataire ? '' : locataire.email}" />
    </div>
    <br/>
    <div>
      <label>Téléphone</label><br/>
      <input type="text" name="telephone" value="${empty locataire ? '' : locataire.telephone}" />
    </div>
    <br/>

    <button type="submit">Enregistrer</button>
    <a href="${pageContext.request.contextPath}/app/locataires">Annuler</a>
  </form>
</div>
</body>
</html>
