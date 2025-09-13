<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html><html lang="fr"><head>
<meta charset="UTF-8"><title>Utilisateurs</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head><body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>
<div class="container mt-4">

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Utilisateurs</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/app/admin/users?action=new">+ Nouvel utilisateur</a>
  </div>

  <c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
  </c:if>

  <table class="table table-sm table-bordered align-middle">
    <thead><tr>
      <th>ID</th><th>Username</th><th>Email</th><th>Rôle</th><th>Actions</th>
    </tr></thead>
    <tbody>
    <c:forEach items="${users}" var="u">
      <tr>
        <td>${u.id}</td>
        <td>${u.username}</td>
        <td>${u.email}</td>
        <td>${u.role}</td>
        <td class="d-flex gap-2">
          <form method="post" action="${pageContext.request.contextPath}/app/admin/users" class="d-flex gap-2">
            <input type="hidden" name="action" value="updateRole"/>
            <input type="hidden" name="id" value="${u.id}"/>
            <select name="role" class="form-select form-select-sm" style="width:auto">
              <option ${u.role=='ADMIN'?'selected':''}>ADMIN</option>
              <option ${u.role=='PROPRIETAIRE'?'selected':''}>PROPRIETAIRE</option>
              <option ${u.role=='LOCATAIRE'?'selected':''}>LOCATAIRE</option>
            </select>
            <button class="btn btn-sm btn-outline-primary">Changer</button>
          </form>

          <form method="post" action="${pageContext.request.contextPath}/app/admin/users" class="d-flex gap-2">
            <input type="hidden" name="action" value="resetPassword"/>
            <input type="hidden" name="id" value="${u.id}"/>
            <input type="password" name="newPassword" placeholder="Nouveau mot de passe" class="form-control form-control-sm" required/>
            <button class="btn btn-sm btn-outline-warning">Reset MDP</button>
          </form>

          <form method="post" action="${pageContext.request.contextPath}/app/admin/users" onsubmit="return confirm('Supprimer cet utilisateur ?')">
            <input type="hidden" name="action" value="delete"/>
            <input type="hidden" name="id" value="${u.id}"/>
            <button class="btn btn-sm btn-outline-danger">Supprimer</button>
          </form>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>
</body></html>
