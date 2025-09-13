<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html><html lang="fr"><head>
<meta charset="UTF-8"><title>Nouvel utilisateur</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head><body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>
<div class="container mt-4" style="max-width:560px">

  <h2>${empty userEdit ? 'Créer un utilisateur' : 'Modifier l’utilisateur'}</h2>

  <c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/app/admin/users">
    <input type="hidden" name="action" value="create"/>
    <div class="mb-3">
      <label class="form-label">Nom d’utilisateur</label>
      <input class="form-control" name="username" value="${userEdit.username}" required/>
    </div>
    <div class="mb-3">
      <label class="form-label">Email</label>
      <input type="email" class="form-control" name="email" value="${userEdit.email}" required/>
    </div>
    <div class="mb-3">
      <label class="form-label">Mot de passe initial</label>
      <input type="password" class="form-control" name="password" minlength="6" required/>
    </div>
    <div class="mb-3">
      <label class="form-label">Rôle</label>
      <select class="form-select" name="role">
        <option value="LOCATAIRE">Locataire</option>
        <option value="PROPRIETAIRE">Propriétaire</option>
        <option value="ADMIN">Admin</option>
      </select>
    </div>
    <div class="d-flex gap-2">
      <button class="btn btn-primary">Enregistrer</button>
      <a class="btn btn-secondary" href="${pageContext.request.contextPath}/app/admin/users">Annuler</a>
    </div>
  </form>

</div>
</body></html>
