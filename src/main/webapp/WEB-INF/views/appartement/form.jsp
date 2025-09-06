<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>
    <c:choose>
      <c:when test="${mode eq 'edit'}">Modifier</c:when>
      <c:otherwise>Créer</c:otherwise>
    </c:choose>
    un appartement
  </title>
  <style>.container{padding:14px}</style>
</head>
<body>

<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>


<div class="container">
  <h2>
    <c:choose>
      <c:when test="${mode eq 'edit'}">Modifier</c:when>
      <c:otherwise>Créer</c:otherwise>
    </c:choose>
    un appartement
  </h2>

  <c:if test="${not empty error}">
    <div style="color:red">${error}</div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/app/appartements">
    <c:choose>
      <c:when test="${mode eq 'edit' && not empty appartement}">
        <input type="hidden" name="id" value="${appartement.id}" />
        <input type="hidden" name="action" value="update" />
      </c:when>
      <c:otherwise>
        <input type="hidden" name="action" value="create" />
      </c:otherwise>
    </c:choose>

    <div>
      <label>Numéro</label><br/>
      <input type="text" name="numero" value="${empty appartement ? '' : appartement.numero}" required />
    </div>
    <br/>

    <div>
      <label>Étage</label><br/>
      <input type="number" name="etage" value="${empty appartement ? '' : appartement.etage}" />
    </div>
    <br/>

    <div>
      <label>Surface (m²)</label><br/>
      <input type="number" step="0.01" name="surface" value="${empty appartement ? '' : appartement.surface}" />
    </div>
    <br/>

    <div>
      <label>Loyer</label><br/>
      <input type="number" step="0.01" name="loyer" value="${empty appartement ? '' : appartement.loyer}" />
    </div>
    <br/>

    <div>
      <label>Immeuble</label><br/>
      <select name="immeubleId" required>
        <option value="">-- choisir --</option>
        <c:forEach items="${immeubles}" var="im">
          <option value="${im.id}"
            <c:if test="${not empty appartement && appartement.immeuble.id == im.id}">selected</c:if>>
            ${im.nom} - ${im.adresse}
          </option>
        </c:forEach>
      </select>
    </div>
    <br/>

    <button type="submit">Enregistrer</button>
    <a href="${pageContext.request.contextPath}/app/appartements">Annuler</a>
  </form>
</div>
</body>
</html>
