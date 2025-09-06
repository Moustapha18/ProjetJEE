<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Accueil</title>
  <style>.container{padding:14px}</style>
</head>
<body>

<%@ include file="/WEB-INF/views/_inc/navbar.jsp" %>

<div class="container">
  <h2>Bienvenue 👋</h2>
  <p>Utilisez le menu pour naviguer.</p>
  <p>
    <a href="${pageContext.request.contextPath}/app/immeubles">➡️ Gérer les immeubles</a>
  </p>
</div>

</body>
</html>
