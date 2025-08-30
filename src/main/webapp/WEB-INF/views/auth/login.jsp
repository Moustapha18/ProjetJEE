<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="https://jakarta.ee/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Connexion</title></head>
<body>
  <h2>Connexion</h2>
  <c:if test="${not empty error}">
    <p style="color:red;"><c:out value="${error}"/></p>
  </c:if>
  <form method="post" action="${pageContext.request.contextPath}/login">
    <label>Nom d'utilisateur</label><br/>
    <input type="text" name="username" required/><br/><br/>
    <label>Mot de passe</label><br/>
    <input type="password" name="password" required/><br/><br/>
    <button type="submit">Se connecter</button>
  </form>
</body>
</html>
