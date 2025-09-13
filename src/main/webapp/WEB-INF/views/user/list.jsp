<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Utilisateurs</title>
  <style>
    .container{padding:14px}
    table{border-collapse:collapse;width:100%}
    th,td{border:1px solid #ddd;padding:6px 10px}
    .muted{color:#64748b}
    form.inline{display:inline-block;margin:0}
    a.btn{padding:6px 10px;border:1px solid #cbd5e1;border-radius:8px;text-decoration:none}
  </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>
<div class="container">
  <h2>Utilisateurs</h2>
  <p><a class="btn" href="${pageContext.request.contextPath}/app/users?action=new">➕ Nouveau</a></p>

  <c:choose>
    <c:when test="${empty users}">
      <p class="muted">Aucun utilisateur.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr><th>ID</th><th>Username</th><th>Email</th><th>Rôle</th><th>Actions</th></tr>
        </thead>
        <tbody>
          <c:forEach items="${users}" var="u">
            <tr>
              <td>${u.id}</td>
              <td>${u.username}</td>
              <td>${u.email}</td>
              <td>${u.role}</td>
              <td>
                <a class="btn" href="${pageContext.request.contextPath}/app/users?action=edit&id=${u.id}">✏️ Modifier</a>
                <form class="inline" method="post" action="${pageContext.request.contextPath}/app/users"
                      onsubmit="return confirm('Supprimer cet utilisateur ?');">
                  <input type="hidden" name="action" value="delete"/>
                  <input type="hidden" name="id" value="${u.id}"/>
                  <button type="submit">🗑️ Supprimer</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
</div>
</body>
</html>
