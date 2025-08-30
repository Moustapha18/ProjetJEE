<%@ taglib uri="https://jakarta.ee/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title><c:out value="${pageTitle!=null?pageTitle:'Location Immeubles'}"/></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/styles.css">
</head>
<body>
  <header>
    <h1>Location Immeubles</h1>
    <nav>
      <a href="${pageContext.request.contextPath}/app/immeubles">Immeubles</a>
      <a href="${pageContext.request.contextPath}/logout">Se déconnecter</a>
    </nav>
  </header>
  <main>
    <!-- contenu -->
    <jsp:doBody/>
  </main>
</body>
</html>
