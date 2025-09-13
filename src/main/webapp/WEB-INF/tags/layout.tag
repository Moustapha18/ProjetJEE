<%@ tag description="Layout commun" pageEncoding="UTF-8" %>
<%@ attribute name="title" required="false" rtexprvalue="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title><c:out value="${title != null ? title : 'Application de Location'}"/></title>

  <!-- CSS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css" />
</head>
<body>
  <!-- NAVBAR (option: mets ton include ici si tu as déjà un _inc/navbar.jsp) -->
  <jsp:include page="/WEB-INF/views/_inc/navbar.jsp" />

  <div class="app-shell d-flex" style="min-height:calc(100vh - 56px)">
    <!-- Si tu as une sidebar réutilisable, include-la ici -->
    <%-- <jsp:include page="/WEB-INF/views/_inc/sidebar.jsp" /> --%>

    <main class="app-main container-fluid py-3">
      <jsp:doBody/>
    </main>
  </div>

  <!-- JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>
