<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
  String ctx = request.getContextPath();
  String uri = request.getRequestURI();

  boolean onHome      = uri.equals(ctx + "/") || uri.equals(ctx + "/index.jsp");
  boolean onDashboard = uri.equals(ctx + "/app") || uri.equals(ctx + "/app/");
  boolean onImmeubles = uri.startsWith(ctx + "/app/immeubles");
  boolean onApparts   = uri.startsWith(ctx + "/app/appartements");
  boolean onContrats  = uri.startsWith(ctx + "/app/contrats");
  boolean onPaiements = uri.startsWith(ctx + "/app/paiements");
  boolean onDemande   = uri.startsWith(ctx + "/app/demandes");
  boolean onReports   = uri.startsWith(ctx + "/app/reports");
  boolean onUsers     = uri.startsWith(ctx + "/app/admin/users");
%>

<c:set var="role" value="${sessionScope.user != null ? sessionScope.user.role : ''}" />

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
<!-- Icônes Bootstrap pour le titre & le bouton Déconnexion -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet"/>

<style>
  .nav-gradient{
    background: linear-gradient(135deg,#0f172a 0%, #111827 60%, #0b1220 100%);
  }
  .navbar{ box-shadow: 0 8px 26px rgba(0,0,0,.18); }
  .navbar-brand{ font-weight:700; letter-spacing:.2px; display:flex; align-items:center; gap:.5rem; }
  .navbar-dark .nav-link{
    color:#cbd5e1; border-bottom:2px solid transparent;
  }
  .navbar-dark .nav-link:hover{ color:#fff; }
  .navbar-dark .nav-link.active{
    color:#fff; border-bottom-color:#22d3ee;
  }
  .navbar-text.badge-user{
    background: rgba(255,255,255,.12);
    border:1px solid rgba(255,255,255,.22);
    border-radius:999px; padding:.25rem .6rem; color:#e5e7eb;
  }
  .btn-logout{ font-weight:700; box-shadow: 0 6px 18px rgba(220,53,69,.35); }
  @media (max-width: 991.98px){
    .navbar-nav .nav-link{ padding-top:.45rem; padding-bottom:.45rem; }
    .navbar-nav.ms-auto{ margin-top:.5rem; }
  }
</style>

<nav class="navbar navbar-expand-lg navbar-dark nav-gradient">
  <div class="container-fluid">
    <!-- ✅ Icône uniquement sur le titre de l’appli -->
    <a class="navbar-brand" href="<%= ctx %>/">
      <i class="bi bi-buildings-fill"></i>
      Location Immeubles
    </a>

    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain"
            aria-controls="navbarMain" aria-expanded="false" aria-label="Basculer la navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarMain">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <!-- Liens sans icônes -->
        <li class="nav-item">
          <a class="nav-link <%= onHome ? "active" : "" %>" href="<%= ctx %>/">Accueil</a>
        </li>

        <!-- LOCATAIRE -->
        <c:if test="${role == 'LOCATAIRE'}">
          <li class="nav-item">
            <a class="nav-link <%= onApparts ? "active" : "" %>" href="<%= ctx %>/app/appartements">Catalogue</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onPaiements ? "active" : "" %>" href="<%= ctx %>/app/paiements">Paiements</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onDemande ? "active" : "" %>" href="<%= ctx %>/app/demandes">Mes demandes</a>
          </li>
        </c:if>

        <!-- PROPRIETAIRE -->
        <c:if test="${role == 'PROPRIETAIRE'}">
          <li class="nav-item">
            <a class="nav-link <%= onImmeubles ? "active" : "" %>" href="<%= ctx %>/app/immeubles">Immeubles</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onApparts ? "active" : "" %>" href="<%= ctx %>/app/appartements">Appartements</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onContrats ? "active" : "" %>" href="<%= ctx %>/app/contrats">Contrats</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onPaiements ? "active" : "" %>" href="<%= ctx %>/app/paiements">Paiements</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onDemande ? "active" : "" %>" href="<%= ctx %>/app/demandes">Demandes</a>
          </li>
        </c:if>

        <!-- ADMIN -->
        <c:if test="${role == 'ADMIN'}">
          <li class="nav-item">
            <a class="nav-link <%= onDashboard ? "active" : "" %>" href="<%= ctx %>/app">Dashboard</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onImmeubles ? "active" : "" %>" href="<%= ctx %>/app/immeubles">Immeubles</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onApparts ? "active" : "" %>" href="<%= ctx %>/app/appartements">Appartements</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onContrats ? "active" : "" %>" href="<%= ctx %>/app/contrats">Contrats</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onPaiements ? "active" : "" %>" href="<%= ctx %>/app/paiements">Paiements</a>
          </li>
          <!-- ✅ Utilisateurs rétabli -->
          <li class="nav-item">
            <a class="nav-link <%= onUsers ? "active" : "" %>" href="<%= ctx %>/app/admin/users">Utilisateurs</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onDemande ? "active" : "" %>" href="<%= ctx %>/app/demandes">Demandes</a>
          </li>
          <li class="nav-item">
            <a class="nav-link <%= onReports ? "active" : "" %>" href="<%= ctx %>/app/reports">Rapports</a>
          </li>
        </c:if>
      </ul>

      <!-- Zone utilisateur -->
      <ul class="navbar-nav ms-auto align-items-center">
        <c:choose>
          <c:when test="${empty sessionScope.user}">
            <li class="nav-item">
              <a class="nav-link" href="<%= ctx %>/login">Se connecter</a>
            </li>
          </c:when>
          <c:otherwise>
            <li class="nav-item me-2">
              <span class="navbar-text badge-user">
                <c:out value="${sessionScope.user.username}"/>
              </span>
            </li>
            <li class="nav-item">
              <!-- ✅ Icône sur Déconnexion -->
              <a class="btn btn-danger btn-sm btn-logout d-inline-flex align-items-center"
                 href="<%= ctx %>/logout">
                <i class="bi bi-box-arrow-right me-2"></i>
                Se déconnecter
              </a>
            </li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </div>
</nav>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
