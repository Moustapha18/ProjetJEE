<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html><html lang="fr"><head>
<meta charset="UTF-8"><title>Réinitialiser le mot de passe</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head><body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container mt-4" style="max-width:520px">
  <h2>Nouveau mot de passe</h2>
  <c:if test="${not empty info}"><div class="alert alert-success">${info}</div></c:if>
  <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
  <form method="post" action="${pageContext.request.contextPath}/reset">
    <input type="hidden" name="token" value="${token}">
    <div class="mb-3">
      <label class="form-label">Nouveau mot de passe</label>
      <input type="password" name="password" class="form-control" required minlength="6">
    </div>
    <div class="mb-3">
      <label class="form-label">Confirmer</label>
      <input type="password" name="confirm" class="form-control" required minlength="6">
    </div>
    <button class="btn btn-success w-100">Mettre à jour</button>
  </form>
</div>
</body></html>
