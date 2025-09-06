<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Accueil</title>
  <style>
 /* Carte du graphique + limites responsives  */
 .chart-section{
   max-width: 960px;
   margin: 12px auto;
   padding: 12px;
   border: 1px solid #e5e7eb;
   border-radius: 10px;
 }

 /* Boîte qui fixe la hauteur du graphe :
    - min 220px (lisible)
    - 45vh (<= 45% de la hauteur écran)
    - max 600px (ne dépassera pas ~20 cm sur la plupart des écrans) */
 .chart-box{
   height: clamp(220px, 45vh, 600px);
 }

 /* Le canvas remplit sa boîte */
 .chart-box canvas{
   width: 100% !important;
   height: 100% !important;
   display: block;
 }


    .container{padding:16px}
    .cards{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:12px;margin:16px 0}
    .card{border:1px solid #e5e7eb;border-radius:10px;padding:14px}
    .muted{color:#6b7280;font-size:14px}
    .big{font-size:24px;font-weight:700}
    .row{display:flex;gap:8px;align-items:center}
    table { border-collapse: collapse }
    th, td { border:1px solid #ddd; padding:6px 10px }
  </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container">
  <h2>Tableau de bord</h2>
  <div class="muted">Période : ${periodeMois}</div>

  <div class="cards">
    <div class="card">
      <div class="muted">Immeubles</div>
      <div class="big">${nbImmeubles}</div>
    </div>
    <div class="card">
      <div class="muted">Appartements (total)</div>
      <div class="big">${nbAppartements}</div>
      <div class="muted">Occupés : ${nbAppartsOccupes} &nbsp;|&nbsp; Libres : ${nbAppartsLibres}</div>
    </div>
    <div class="card">
      <div class="muted">Locataires</div>
      <div class="big">${nbLocataires}</div>
    </div>
    <div class="card">
      <div class="muted">Contrats actifs</div>
      <div class="big">${nbContratsActifs}</div>
    </div>
    <div class="card">
      <div class="muted">Loyer mensuel théorique (contrats actifs)</div>
      <div class="big"><c:out value="${loyerMensuelTotal}"/> FCFA</div>
    </div>
    <div class="card">
      <div class="muted">Paiements encaissés (${periodeMois})</div>
      <div class="big"><c:out value="${paiementsMois}"/> FCFA</div>
    </div>
  </div>

  <hr/>
  <h3>Contrats qui expirent sous 30 jours</h3>
  <c:choose>
    <c:when test="${empty contratsQuiExpirent}">
      <p>Aucun contrat n’expire dans les 30 prochains jours.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Appartement</th>
            <th>Locataire</th>
            <th>Date fin</th>
            <th>Loyer (FCFA)</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach items="${contratsQuiExpirent}" var="c">
          <tr>
            <td>${c.id}</td>
            <td>${c.appartement.numero} (${c.appartement.immeuble.nom})</td>
            <td><c:out value="${c.locataire.nomComplet}"/></td>
            <td>${c.dateFin}</td>
            <td>${c.loyerMensuel}</td>
            <td>
              <a href="${pageContext.request.contextPath}/app/contrats?action=edit&id=${c.id}">
                Gérer
              </a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>

  <hr/>
  <h3>Contrats actifs sans paiement ce mois</h3>
  <c:choose>
    <c:when test="${empty contratsEnRetard}">
      <p>🎉 Aucun retard de paiement pour ${periodeMois}.</p>
    </c:when>
    <c:otherwise>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Appartement</th>
            <th>Locataire</th>
            <th>Loyer (FCFA)</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach items="${contratsEnRetard}" var="c">
          <tr>
            <td>${c.id}</td>
            <td>${c.appartement.numero} (${c.appartement.immeuble.nom})</td>
             <td><c:out value="${c.locataire.nomComplet}"/></td>
            <td>${c.loyerMensuel}</td>
            <td>
              <a href="${pageContext.request.contextPath}/app/paiements?action=new&contratId=${c.id}">
                Enregistrer un paiement
              </a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>

  <hr/>
  <hr/>
  <h3>Paiements encaissés — 6 derniers mois</h3>

  <div class="chart-section">
    <div class="chart-box">
      <canvas id="chart6mois"></canvas>
    </div>
  </div>


  <!-- On dépose le JSON (ou [] par défaut) dans des balises dédiées -->
  <c:set var="labelsJson" value="${empty chartLabels ? '[]' : chartLabels}"/>
  <c:set var="dataJson"   value="${empty chartData   ? '[]' : chartData}"/>

  <script type="application/json" id="labelsJson"><c:out value="${labelsJson}" escapeXml="false"/></script>
  <script type="application/json" id="dataJson"><c:out value="${dataJson}" escapeXml="false"/></script>

  <!-- Chart.js -->
  <script type="application/json" id="labelsJson"><c:out value="${empty chartLabels ? '[]' : chartLabels}" escapeXml="false"/></script>
  <script type="application/json" id="dataJson"><c:out value="${empty chartData ? '[]' : chartData}" escapeXml="false"/></script>

  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
  <script>
    function safeParseJson(elId, fallback) {
      try {
        const el = document.getElementById(elId);
        const txt = (el && el.textContent || '').trim();
        return txt ? JSON.parse(txt) : fallback;
      } catch (e) { console.error("JSON invalide pour", elId, e); return fallback; }
    }

    const labels = safeParseJson('labelsJson', []);
    const data   = (safeParseJson('dataJson', []) || []).map(v => (v == null ? 0 : +v));

    const ctx = document.getElementById('chart6mois');
    if (ctx) {
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels,
          datasets: [{
            label: 'Paiements (FCFA)',
            data,
            borderWidth: 1,
            backgroundColor: 'rgba(54, 162, 235, 0.35)',
            borderColor: 'rgba(54, 162, 235, 1)'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false, // indispensable pour respecter la hauteur CSS
          scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
          plugins: { legend: { display: true, position: 'top' }, tooltip: { enabled: true } }
        }
      });
    } else {
      console.error("Canvas introuvable");
    }
  </script>

  <div class="row" style="margin-top:12px">
    <a href="${pageContext.request.contextPath}/app/immeubles">→ Gérer les immeubles</a>
    <span class="muted">|</span>
    <a href="${pageContext.request.contextPath}/app/appartements">→ Gérer les appartements</a>
    <span class="muted">|</span>
    <a href="${pageContext.request.contextPath}/app/locataires">→ Gérer les locataires</a>
    <span class="muted">|</span>
    <a href="${pageContext.request.contextPath}/app/contrats">→ Gérer les contrats</a>
    <span class="muted">|</span>
    <a href="${pageContext.request.contextPath}/app/paiements">→ Gérer les paiements</a>
  </div>
</div>
</body>
</html>
