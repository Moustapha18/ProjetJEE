<!-- src/main/webapp/WEB-INF/views/immeuble/form.jsp -->
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>
    <c:choose><c:when test="${mode eq 'edit'}">Modifier</c:when><c:otherwise>Créer</c:otherwise></c:choose>
    un immeuble
  </title>
  <style>.container{padding:14px} .row{margin-bottom:10px}</style>
</head>
<body>

<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>

<div class="container">
  <h2>
    <c:choose><c:when test="${mode eq 'edit'}">Modifier</c:when><c:otherwise>Créer</c:otherwise></c:choose>
    un immeuble
  </h2>

  <c:if test="${not empty error}">
    <div style="color:red">${error}</div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/app/immeubles">
    <c:choose>
      <c:when test="${mode eq 'edit' && not empty immeuble}">
        <input type="hidden" name="id" value="${immeuble.id}" />
        <input type="hidden" name="action" value="update" />
      </c:when>
      <c:otherwise>
        <input type="hidden" name="action" value="create" />
      </c:otherwise>
    </c:choose>

    <div class="row">
      <label>Nom *</label><br/>
      <input type="text" name="nom" value="${empty immeuble ? '' : immeuble.nom}" required />
    </div>

    <div class="row">
      <label>Adresse *</label><br/>
      <input type="text" name="adresse" value="${empty immeuble ? '' : immeuble.adresse}" required />
    </div>

    <div class="row">
      <label>Ville *</label><br/>
      <input type="text" name="ville" value="${empty immeuble ? '' : immeuble.ville}" required />
    </div>

    <div class="row">
      <label>Code postal</label><br/>
      <input type="text" name="codePostal" value="${empty immeuble ? '' : immeuble.codePostal}" />
    </div>

    <div class="row">
      <label>Description</label><br/>
      <textarea name="description" rows="3">${empty immeuble ? '' : immeuble.description}</textarea>
    </div>

    <div class="row">
      <label>Année de construction</label><br/>
      <input type="number" name="anneeConstruction" value="${empty immeuble ? '' : immeuble.anneeConstruction}" />
    </div>

    <div class="row">
      <label>Nombre d'étages</label><br/>
      <input type="number" name="nbEtages" value="${empty immeuble ? '' : immeuble.nbEtages}" />
    </div>

    <div class="row">
      <label>Nombre d'appartements</label><br/>
      <input type="number" name="nbAppartements" value="${empty immeuble ? '' : immeuble.nbAppartements}" />
    </div>

    <div class="row">
      <label>Surface totale (m²)</label><br/>
      <input type="number" step="0.01" name="surfaceTotale" value="${empty immeuble ? '' : immeuble.surfaceTotale}" />
    </div>

    <button type="submit">Enregistrer</button>
    <a href="${pageContext.request.contextPath}/app/immeubles">Annuler</a>
  </form>
</div>
</body>
</html>
