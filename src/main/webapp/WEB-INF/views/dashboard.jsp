<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Tableau de bord</title>

  <!-- Bootstrap & Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body {
      background: #f4f6f9;
    }
    h2, h3 {
      font-weight: 600;
    }
    .card-kpi {
      border: none;
      border-radius: 1rem;
      box-shadow: 0 6px 18px rgba(0,0,0,0.08);
      color: #fff;
    }
    .card-kpi .h3 {
      font-weight: bold;
    }
    .kpi-blue { background: linear-gradient(135deg,#0d6efd,#458ff9); }
    .kpi-green { background: linear-gradient(135deg,#198754,#42ba96); }
    .kpi-orange { background: linear-gradient(135deg,#fd7e14,#f9a23c); }
    .kpi-purple { background: linear-gradient(135deg,#6f42c1,#9b6ef3); }
    .kpi-pink { background: linear-gradient(135deg,#d63384,#f06595); }
    .kpi-teal { background: linear-gradient(135deg,#20c997,#63e6be); }

    .chart-box {
      height: clamp(220px, 45vh, 600px);
    }
    .table thead {
      background-color: #e9ecef;
    }
    .badge-paid {
      background-color: #d1e7dd;
      color: #0f5132;
      font-size: 0.8rem;
      border-radius: 0.5rem;
    }
    .badge-warning {
      background-color: #fff3cd;
      color: #664d03;
    }
    .badge-danger {
      background-color: #f8d7da;
      color: #842029;
    }
  </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/_inc/navbar.jsp"/>

<div class="container my-4">

  <!-- En-tête -->
  <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
    <h2 class="mb-2">📊 Tableau de bord</h2>
    <span class="text-muted">Période : ${periodeMois}</span>
  </div>

  <!-- KPIs avec couleurs -->
  <div class="row g-3 mb-4">
    <div class="col-12 col-sm-6 col-lg-4">
      <div class="card card-kpi kpi-blue p-3">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <div>Immeubles</div>
            <div class="h3 m-0">${nbImmeubles}</div>
          </div>
          <i class="bi bi-buildings fs-1"></i>
        </div>
      </div>
    </div>

    <div class="col-12 col-sm-6 col-lg-4">
      <div class="card card-kpi kpi-green p-3">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <div>Appartements</div>
            <div class="h3 m-0">${nbAppartements}</div>
            <small>Occupés : ${nbAppartsOccupes} | Libres : ${nbAppartsLibres}</small>
          </div>
          <i class="bi bi-door-open fs-1"></i>
        </div>
      </div>
    </div>

    <div class="col-12 col-sm-6 col-lg-4">
      <div class="card card-kpi kpi-orange p-3">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <div>Locataires</div>
            <div class="h3 m-0">${nbLocataires}</div>
          </div>
          <i class="bi bi-people fs-1"></i>
        </div>
      </div>
    </div>

    <div class="col-12 col-sm-6 col-lg-4">
      <div class="card card-kpi kpi-purple p-3">
        <div>Contrats actifs</div>
        <div class="h3 m-0">${nbContratsActifs}</div>
      </div>
    </div>

    <div class="col-12 col-sm-6 col-lg-4">
      <div class="card card-kpi kpi-pink p-3">
        <div>Loyer mensuel théorique</div>
        <div class="h3 m-0">${loyerMensuelTotal} FCFA</div>
      </div>
    </div>

    <div class="col-12 col-sm-6 col-lg-4">
      <div class="card card-kpi kpi-teal p-3">
        <div>Paiements encaissés (${periodeMois})</div>
        <div class="h3 m-0">${paiementsMois} FCFA</div>
      </div>
    </div>
  </div>

  <!-- Contrats qui expirent -->
  <div class="card mb-4 shadow-sm">
    <div class="card-header fw-semibold bg-light"><i class="bi bi-hourglass-split"></i> Contrats qui expirent</div>
    <div class="table-responsive">
      <table class="table align-middle mb-0">
        <thead>
          <tr><th>ID</th><th>Appartement</th><th>Locataire</th><th>Date fin</th><th>Loyer</th><th></th></tr>
        </thead>
        <tbody>${tableContratsExpirent}</tbody>
      </table>
    </div>
  </div>

  <!-- Retards -->
  <div class="card mb-4 shadow-sm">
    <div class="card-header fw-semibold bg-light"><i class="bi bi-exclamation-triangle"></i> Retards de paiement</div>
    <div class="table-responsive">
      <table class="table align-middle mb-0">
        <thead>
          <tr><th>ID</th><th>Appartement</th><th>Locataire</th><th>Loyer</th><th></th></tr>
        </thead>
        <tbody>${tableContratsRetard}</tbody>
      </table>
    </div>
  </div>

  <!-- Graphique -->
  <div class="card shadow-sm">
    <div class="card-header bg-light fw-semibold"><i class="bi bi-bar-chart"></i> Évolution des paiements</div>
    <div class="card-body">
      <div class="chart-box">
        <canvas id="chart6mois"></canvas>
      </div>
    </div>
  </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
  const labels = ${chartLabels != null ? chartLabels : "[]"};
  const data   = ${chartData != null ? chartData : "[]"};

  new Chart(document.getElementById('chart6mois'), {
    type: 'bar',
    data: {
      labels,
      datasets: [{
        label: 'Paiements (FCFA)',
        data,
        backgroundColor: 'rgba(13,110,253,0.6)',
        borderColor: 'rgba(13,110,253,1)',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: { y: { beginAtZero: true } }
    }
  });
</script>

</body>
</html>
