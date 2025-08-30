<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Nouvel immeuble</title></head>
<body>
  <h2>Créer un immeuble</h2>
  <form method="post" action="${pageContext.request.contextPath}/app/immeubles">
    <label>Nom</label><br/>
    <input type="text" name="nom" required/><br/>
    <label>Adresse</label><br/>
    <input type="text" name="adresse"/><br/><br/>
    <button type="submit">Enregistrer</button>
  </form>
</body>
</html>
