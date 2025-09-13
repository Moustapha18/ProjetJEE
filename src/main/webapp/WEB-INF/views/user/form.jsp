<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Utilisateur</title>
  <style>
    .wrap{max-width:540px;margin:20px auto;font-family:system-ui}
    .card{border:1px solid #e5e7eb;border-radius:10px;padding:16px}
    label{display:block;margin:8px 0 4px}
    input,select{width:100%;padding:8px;border:1px solid #cbd5e1;border-radius:8px}
    button{margin-top:12px;padding:8px 12px;border:0;background:#0f172a;color:#fff;border-radius:8px}
  </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="wrap">
  <h2><c:out value="${mode == 'edit' ? 'Modifier' : 'Créer'}"/> un utilisateur</h2>
  <div class="card">
    <form method="post" action="${pageContext.request.contextPath}/app/users">
      <c:choose>
        <c:when test="${mode == 'edit'}">
          <input type="hidden" name="action" value="update"/>
          <input type="hidden" name="id" value="${u.id}"/>
        </c:when>
        <c:otherwise>
          <input type="hidden" name="action" value="create"/>
        </c:otherwise>
      </c:choose>

      <label>Username</label>
      <input name="username" value="${u.username}" required />

      <label>Email</label>
      <input type="email" name="email" value="${u.email}" required />

      <label>Rôle</label>
      <select name="role">
        <option value="LOCATAIRE" ${u.role == 'LOCATAIRE' ? 'selected' : ''}>Locataire</option>
        <option value="PROPRIETAIRE" ${u.role == 'PROPRIETAIRE' ? 'selected' : ''}>Propriétaire</option>
        <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
      </select>

      <label>Mot de passe <span class="muted">(laisse vide pour conserver)</span></label>
      <input type="password" name="password"/>

      <button type="submit">Enregistrer</button>
    </form>
  </div>
</div>
</body>
</html>
