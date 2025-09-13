<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  String ctx = request.getContextPath();
  Object user = session.getAttribute("user");
  if (user == null) {
    response.sendRedirect(ctx + "/login");
    return;
  }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Accueil | Location Immeubles</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
  <jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>
  <div class="container mt-4">
    <h1>🏢 Bienvenue sur Location Immeubles</h1>
    <p>Retrouvez le catalogue, les demandes, les contrats, les paiements et, si vous êtes admin, les rapports.</p>
    <a class="btn btn-success" href="<%= ctx %>/app">Aller au tableau de bord</a>
  </div>
</body>
</html>
