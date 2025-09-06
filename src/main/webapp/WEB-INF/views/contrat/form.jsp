<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>
    <c:choose><c:when test="${mode eq 'edit'}">Modifier</c:when><c:otherwise>Nouveau</c:otherwise></c:choose>
    contrat
  </title>
  <style>.container{padding:14px}</style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container">
  <h2>
    <c:choose><c:when test="${mode eq 'edit'}">Modifier</c:when><c:otherwise>Nouveau</c:otherwise></c:choose>
    contrat
  </h2>

  <c:if test="${not empty error}"><div style="color:red">${error}</div></c:if>

  <form method="post" action="${pageContext.request.contextPath}/app/contrats">
    <c:choose>
      <c:when test="${mode eq 'edit' && not empty contrat}">
        <input type="hidden" name="id" value="${contrat.id}" />
        <input type="hidden" name="action" value="update" />
      </c:when>
      <c:otherwise><input type="hidden" name="action" value="create" /></c:otherwise>
    </c:choose>

    <!-- Appartement -->
    <div>
      <label>Appartement</label><br/>
      <select name="appartementId" required>
        <option value="">-- choisir --</option>
        <c:forEach items="${appartements}" var="a">
          <option value="${a.id}" <c:if test="${not empty contrat && contrat.appartement.id == a.id}">selected</c:if>>
            ${a.numero} - ${a.immeuble.nom}
          </option>
        </c:forEach>
      </select>
    </div>
    <br/>

    <!-- Locataire (⚠️ utilise nomComplet) -->
    <div>
      <label>Locataire</label><br/>
      <select name="locataireId" required>
        <option value="">-- choisir --</option>
        <c:forEach items="${locataires}" var="l">
          <option value="${l.id}" <c:if test="${not empty contrat && contrat.locataire.id == l.id}">selected</c:if>>
            ${l.nomComplet}
          </option>
        </c:forEach>
      </select>
    </div>
    <br/>

    <div>
      <label>Date début</label><br/>
      <input type="date" name="dateDebut" value="${empty contrat ? '' : contrat.dateDebut}" required />
    </div>
    <br/>

    <div>
      <label>Date fin (optionnel)</label><br/>
      <input type="date" name="dateFin" value="${empty contrat ? '' : contrat.dateFin}" />
    </div>
    <br/>

    <div>
      <label>Loyer mensuel (FCFA)</label><br/>
      <input type="number" step="0.01" name="loyerMensuel" value="${empty contrat ? '' : contrat.loyerMensuel}" required />
    </div>
    <br/>

    <div>
      <label>Caution (FCFA)</label><br/>
      <input type="number" step="0.01" name="caution" value="${empty contrat ? '' : contrat.caution}" />
    </div>
    <br/>

    <div>
      <label>Statut</label><br/>
      <select name="statut" required>
        <c:forEach items="${statuts}" var="s">
          <option value="${s}" <c:if test="${not empty contrat && contrat.statut == s}">selected</c:if>>${s}</option>
        </c:forEach>
      </select>
    </div>
    <br/>

    <button type="submit">Enregistrer</button>
    <a href="${pageContext.request.contextPath}/app/contrats">Annuler</a>
  </form>
</div>
</body>
</html>
