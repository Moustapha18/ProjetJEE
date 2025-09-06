<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Inscription</title></head>
<body>
  <h2>Créer un compte</h2>
  <form method="post" action="${pageContext.request.contextPath}/register">
    <label>Nom d'utilisateur</label><br/>
    <input type="text" name="username" required/><br/>
    <label>Email</label><br/>
    <input type="email" name="email" required/><br/>
    <label>Mot de passe</label><br/>
    <input type="password" name="password" required/><br/><br/>
    <button type="submit">S'inscrire</button>
  </form>
  <p><a href="${pageContext.request.contextPath}/login">Déjà un compte ? Se connecter</a></p>
</body>
</html>
