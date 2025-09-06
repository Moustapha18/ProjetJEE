<%@ page contentType="text/html; charset=UTF-8" %>
<%
  String ctx = request.getContextPath();
  String uri = request.getRequestURI();

  boolean onHome      = uri.equals(ctx + "/") || uri.equals(ctx + "/index.jsp");
  boolean onDashboard = uri.equals(ctx + "/app") || uri.equals(ctx + "/app/");
  boolean onImmeubles = uri.startsWith(ctx + "/app/immeuble");
  boolean onApparts   = uri.startsWith(ctx + "/app/appartement");
  boolean onLocataires= uri.startsWith(ctx + "/app/locataire");
  boolean onContrats  = uri.startsWith(ctx + "/app/contrat");
  boolean onPaiements = uri.startsWith(ctx + "/app/paiement");
%>
<style>
  .topbar{display:flex;gap:14px;align-items:center;padding:10px 14px;background:#0f172a;color:#fff}
  .topbar a{color:#cbd5e1;text-decoration:none;padding:6px 10px;border-radius:6px}
  .topbar a.active{background:#334155;color:#fff}
  .spacer{flex:1}
</style>
<nav class="topbar">
  <strong>🏢 Location Immeubles</strong>
  <a href="<%= ctx %>/" class="<%= onHome ? "active" : "" %>">Accueil</a>
  <a href="<%= ctx %>/app" class="<%= onDashboard ? "active" : "" %>">Dashboard</a>
  <a href="<%= ctx %>/app/immeubles" class="<%= onImmeubles ? "active" : "" %>">Immeubles</a>
  <a href="<%= ctx %>/app/appartements" class="<%= onApparts ? "active" : "" %>">Appartements</a>
  <a href="<%= ctx %>/app/locataires" class="<%= onLocataires ? "active" : "" %>">Locataires</a>
  <a href="<%= ctx %>/app/contrats" class="<%= onContrats ? "active" : "" %>">Contrats</a>
  <a href="<%= ctx %>/app/paiements" class="<%= onPaiements ? "active" : "" %>">Paiements</a>
  <span class="spacer"></span>
</nav>
