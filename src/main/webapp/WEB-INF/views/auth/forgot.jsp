<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html><html lang="fr"><head>
<meta charset="UTF-8"><title>Mot de passe oublié</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head><body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container mt-4" style="max-width:520px">
  <h2>Mot de passe oublié</h2>
  <c:if test="${not empty info}"><div class="alert alert-success">${info}</div></c:if>
  <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
  <form method="post" action="${pageContext.request.contextPath}/forgot">
    <div class="mb-3">
      <label class="form-label">Votre email</label>
      <input type="email" name="email" class="form-control" required>
    </div>
    <button class="btn btn-primary w-100">Recevoir le lien</button>
  </form>
  <p class="mt-3"><a href="${pageContext.request.contextPath}/login">Retour connexion</a></p>
</div>
</body></html>
