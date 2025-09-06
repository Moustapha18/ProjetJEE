<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>
    <c:choose><c:when test="${mode eq 'edit'}">Modifier</c:when><c:otherwise>Nouveau</c:otherwise></c:choose>
    paiement
  </title>
  <style>.container{padding:14px}</style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container">
  <h2>
    <c:choose><c:when test="${mode eq 'edit'}">Modifier</c:when><c:otherwise>Nouveau</c:otherwise></c:choose>
    paiement
  </h2>

  <c:if test="${not empty error}"><div style="color:red">${error}</div></c:if>

  <form method="post" action="${pageContext.request.contextPath}/app/paiements">
    <c:choose>
      <c:when test="${mode eq 'edit' && not empty paiement}">
        <input type="hidden" name="id" value="${paiement.id}" />
        <input type="hidden" name="action" value="update" />
      </c:when>
      <c:otherwise>
        <input type="hidden" name="action" value="create" />
      </c:otherwise>
    </c:choose>

    <div>
      <label>Contrat</label><br/>
      <select name="contratId" required>
        <option value="">-- choisir --</option>
        <c:forEach items="${contrats}" var="c">
          <option value="${c.id}"
                  <c:if test="${not empty paiement && paiement.contrat.id == c.id}">selected</c:if>>
            #${c.id} — ${c.locataire.nomComplet} — ${c.appartement.numero} (${c.appartement.immeuble.nom})
          </option>
        </c:forEach>
      </select>
    </div>
    <br/>

    <div>
      <label>Date de paiement</label><br/>
      <input type="date" name="datePaiement"
             value="<c:out value='${empty paiement ? "" : paiement.datePaiement}'/>" required />
    </div>
    <br/>

    <div>
      <label>Montant (FCFA)</label><br/>
      <input type="number" step="0.01" name="montant"
             value="<c:out value='${empty paiement ? "" : paiement.montant}'/>" required />
    </div>
    <br/>

    <div>
      <label>Mode</label><br/>
      <select name="mode" required>
        <c:forEach items="${modes}" var="m">
          <option value="${m}" <c:if test="${not empty paiement && paiement.mode == m}">selected</c:if>>${m}</option>
        </c:forEach>
      </select>
    </div>
    <br/>

    <div>
      <label>Statut</label><br/>
      <select name="statut" required>
        <c:forEach items="${statuts}" var="s">
          <option value="${s}" <c:if test="${not empty paiement && paiement.statut == s}">selected</c:if>>${s}</option>
        </c:forEach>
      </select>
    </div>
    <br/>

    <div>
      <label>Référence (optionnel)</label><br/>
      <input type="text" name="reference" value="<c:out value='${empty paiement ? "" : paiement.reference}'/>" />
    </div>
    <br/>

    <button type="submit">Enregistrer</button>
    <a href="${pageContext.request.contextPath}/app/paiements">Annuler</a>
  </form>
</div>
</body>
</html>
