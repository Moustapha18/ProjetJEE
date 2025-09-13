<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt"  prefix="fmt" %>
<jsp:useBean id="now" class="java.util.Date" />

<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Reçu de paiement</title>
  <style>
    .wrap{max-width:720px;margin:20px auto;padding:20px;border:1px solid #e5e7eb;border-radius:10px;font-family:system-ui,Segoe UI,Arial}
    .muted{color:#64748b}
    .row{display:flex;justify-content:space-between}
    .h{margin:0 0 10px 0}
    .box{background:#f8fafc;padding:10px;border-radius:8px}
    .right{text-align:right}
  </style>
</head>
<body>
<div class="wrap">
  <div class="row">
    <h2 class="h">Reçu de paiement</h2>
    <div class="right muted">#<c:out value="${p.id}"/></div>
  </div>

  <p class="muted">
    Date d’émission :
    <fmt:formatDate value="${now}" pattern="dd/MM/yyyy HH:mm"/>
  </p>

  <div class="box">
    <p>
      <strong>Locataire:</strong>
      <c:out value="${p.contrat.locataire.nomComplet}"/>
      &lt;<c:out value="${p.contrat.locataire.email}"/>&gt;
    </p>
    <p>
      <strong>Appartement:</strong>
      <c:out value="${p.contrat.appartement.numero}"/> —
      <c:out value="${p.contrat.appartement.immeuble.nom}"/>,
      <c:out value="${p.contrat.appartement.immeuble.ville}"/>
    </p>
    <p><strong>Échéance:</strong> <c:out value="${p.echeance}"/></p>
    <p><strong>Montant réglé:</strong> <c:out value="${p.montant}"/></p>
    <p><strong>Référence:</strong> <c:out value="${p.reference}"/></p>
    <p>
      <strong>Date de paiement:</strong>
      <c:choose>
        <c:when test="${p.datePaiement != null}">
          <c:out value="${p.datePaiement}"/>
        </c:when>
        <c:otherwise>—</c:otherwise>
      </c:choose>
    </p>
  </div>

  <p class="muted">Merci de votre paiement.</p>
</div>
<script>window.print()</script>
</body>
</html>
