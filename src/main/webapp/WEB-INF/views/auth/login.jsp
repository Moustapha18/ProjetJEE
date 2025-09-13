<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Connexion | Location Immeubles</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <!-- Icônes Bootstrap (pour l’icône d’immeuble) -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    :root{
      --bg-start:#0ea5e9; /* sky-500 */
      --bg-end:#7c3aed;   /* violet-600 */
      --card-bg:rgba(255,255,255,.12);
      --glass-border:1px solid rgba(255,255,255,.22);
      --text:#0f172a;
      --muted:#6b7280;
      --focus:#2563eb;
      --ok:#16a34a;
      --danger:#dc2626;
      --shadow:0 20px 50px rgba(0,0,0,.25);
    }
    @media (prefers-color-scheme: dark){
      :root{ --text:#e5e7eb; --muted:#a1a1aa; --card-bg:rgba(17,24,39,.6); --glass-border:1px solid rgba(255,255,255,.08); }
    }

    *{ box-sizing: border-box; }
    html, body { height:100%; }
    body {
      margin: 0;
      font-family: Inter, system-ui, -apple-system, Segoe UI, Roboto, "Helvetica Neue", Arial, "Noto Sans", "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
      color: var(--text);
      background: radial-gradient(1200px 800px at 10% 10%, rgba(255,255,255,.18), transparent) ,
                  radial-gradient(1000px 700px at 90% 30%, rgba(255,255,255,.12), transparent),
                  linear-gradient(135deg, var(--bg-start), var(--bg-end));
      display:flex; align-items:center; justify-content:center; padding: 28px;
    }

    .auth-shell{
      width: 100%;
      max-width: 980px;
      display: grid;
      grid-template-columns: 1.1fr .9fr;
      gap: 24px;
    }
    @media (max-width: 920px){
      .auth-shell{ grid-template-columns: 1fr; max-width: 560px; }
    }

    /* Panneau visuel (gauche) */
    .hero{
      position: relative;
      border-radius: 22px;
      padding: 28px;
      color: white;
      overflow:hidden;
      min-height: 520px;
      background:
        radial-gradient(1200px 800px at -20% -10%, rgba(255,255,255,.08), transparent),
        radial-gradient(800px 600px at 120% 20%, rgba(255,255,255,.08), transparent),
        linear-gradient(145deg, rgba(255,255,255,.14), rgba(255,255,255,.06));
      border: var(--glass-border);
      box-shadow: var(--shadow);
      backdrop-filter: blur(12px);
    }
    .hero .badge{
      display:inline-flex; align-items:center; gap:.5rem;
      background: rgba(255,255,255,.16);
      border: 1px solid rgba(255,255,255,.28);
      color:#fff;
      font-weight:600;
      padding:.4rem .7rem;
      border-radius: 999px;
      font-size:.88rem;
    }
    .hero h1{
      font-size: clamp(1.6rem, 1.1rem + 1.8vw, 2.2rem);
      margin: 16px 0 8px;
      line-height: 1.15;
      color:#fff;
    }
    .hero p{ margin:0; opacity:.95 }
    .hero-illu{
      position:absolute; inset:auto -60px -60px auto; width: 300px; aspect-ratio: 1;
      background: radial-gradient(closest-side at 50% 50%, rgba(255,255,255,.25), rgba(255,255,255,0)),
                  conic-gradient(from 180deg at 50% 50%, rgba(255,255,255,.18), rgba(255,255,255,0) 55%);
      filter: blur(8px);
      transform: rotate(20deg);
    }
    .hero-icon{
      display:inline-grid; place-items:center;
      width:56px; height:56px; border-radius:16px;
      background: linear-gradient(135deg, #22d3ee, #22c55e);
      border: 1px solid rgba(255,255,255,.35);
      color:#062028;
      box-shadow: 0 10px 30px rgba(0,0,0,.25), inset 0 0 18px rgba(255,255,255,.26);
      font-size: 26px;
    }

    /* Carte connexion (droite) */
    .card{
      border-radius: 22px;
      padding: 26px 26px 22px;
      background: var(--card-bg);
      border: var(--glass-border);
      box-shadow: var(--shadow);
      backdrop-filter: blur(14px);
    }
    .brand{
      display:flex; align-items:center; gap:.75rem; margin-bottom: 14px;
    }
    .brand .logo{
      display:inline-grid; place-items:center;
      width:48px; height:48px; border-radius:14px;
      background: linear-gradient(135deg, #22d3ee, #a78bfa);
      border: 1px solid rgba(255,255,255,.4);
      color:#0b1220; font-size: 22px;
      box-shadow: inset 0 0 14px rgba(255,255,255,.28);
    }
    .brand .title{
      display:flex; flex-direction:column; line-height:1.1;
    }
    .brand .title b{ font-size: 1.15rem; letter-spacing:.2px }
    .brand .title span{ color: var(--muted); font-size:.92rem }

    .card h2{ margin: 10px 0 14px; font-size: 1.3rem; }
    .form-group{ margin-bottom: 14px; }
    label{ display:block; font-weight: 600; margin-bottom: .45rem; }
    .input{
      width:100%; padding: .72rem .9rem; border-radius: 12px;
      border:1px solid rgba(0,0,0,.12);
      background: rgba(255,255,255,.85);
      font-size: 1rem; outline: none;
    }
    .input:focus{ border-color: var(--focus); box-shadow: 0 0 0 3px rgba(37,99,235,.16); }

    .input-group{
      position:relative;
    }
    .toggle-eye{
      position:absolute; right:10px; top:50%; transform: translateY(-50%);
      border:none; background: transparent; cursor:pointer;
      color:#475569; font-size: 1.1rem;
      padding: 6px; border-radius: 8px;
    }
    .toggle-eye:hover{ background: rgba(2,6,23,.06) }

    .btn{
      width:100%; padding: .85rem 1rem; border:none; cursor:pointer;
      border-radius: 12px; color:#fff; font-size: 1rem; font-weight: 700;
      background: linear-gradient(135deg, #2563eb, #7c3aed);
      box-shadow: 0 10px 25px rgba(124,58,237,.28);
    }
    .btn:hover{ filter: brightness(1.05); }
    .btn:disabled{ opacity:.7; cursor: progress; }

    .row-between{ display:flex; justify-content:space-between; align-items:center; gap: 12px; }
    .small{ font-size:.92rem; color: var(--muted); }

    .alert{
      border-radius: 12px; padding:.75rem .9rem; margin: 10px 0 16px; font-size:.95rem;
      border:1px solid transparent; background:#f8fafc; color:#0f172a;
    }
    .alert.error{ background:#fef2f2; border-color:#fecaca; color:#7f1d1d; }
    .alert.success{ background:#ecfdf5; border-color:#a7f3d0; color:#065f46; }

    .links{ margin-top: 14px; display:flex; justify-content:space-between; gap: 10px; flex-wrap: wrap; }
    .links a{ color:#0ea5e9; text-decoration:none; font-weight:600 }
    .links a:hover{ text-decoration:underline; }

    /* Petit badge de succès en pied de carte (option esthétique) */
    .trust{
      margin-top: 16px; display:flex; align-items:center; gap:8px; color: var(--muted); font-size:.9rem;
    }
    .trust i{ color: #22c55e; }
  </style>
</head>
<body>

  <div class="auth-shell">
    <!-- Panneau gauche : hero -->
    <div class="hero" aria-hidden="true">
      <span class="badge">
        <i class="bi bi-buildings-fill"></i>  Location Immeubles
      </span>
      <h1>Gérez vos biens et vos loyers sans friction.</h1>
      <p>Une expérience fluide pour les propriétaires et les locataires : réservations, contrats, paiements & rapports — au même endroit.</p>

      <div class="hero-illu"></div>
    </div>

    <!-- Carte de connexion -->
    <div class="card" role="form" aria-labelledby="login-title">
      <div class="brand">
        <div class="logo"><i class="bi bi-buildings"></i></div>
        <div class="title">
          <b>Location Immeubles</b>
          <span>Espace sécurisé</span>
        </div>
      </div>

      <h2 id="login-title">Connexion</h2>

      <c:if test="${not empty error}">
        <div class="alert error" role="alert">
          ${error}
        </div>
      </c:if>
      <c:if test="${not empty info}">
        <div class="alert success" role="status">
          ${info}
        </div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/login" id="loginForm" autocomplete="on">
        <div class="form-group">
          <label for="username">Nom d'utilisateur</label>
          <input class="input" type="text" name="username" id="username" required autofocus autocomplete="username" />
        </div>

        <div class="form-group input-group">
          <label for="password">Mot de passe</label>
          <input class="input" type="password" name="password" id="password" required autocomplete="current-password" />
          <button class="toggle-eye" type="button" aria-label="Afficher le mot de passe" title="Afficher/masquer">
            <i class="bi bi-eye"></i>
          </button>
        </div>

        <button class="btn" type="submit" id="submitBtn">
          <i class="bi bi-box-arrow-in-right"></i> Se connecter
        </button>

        <div class="links">
          <a href="${pageContext.request.contextPath}/forgot">Mot de passe oublié ?</a>
          <a href="${pageContext.request.contextPath}/register">Créer un compte</a>
        </div>

        <div class="trust">
          <i class="bi bi-shield-check"></i>
          Connexion chiffrée • Données protégées
        </div>
      </form>
    </div>
  </div>

  <script>
    // Toggle show/hide password
    (function(){
      const pwd = document.getElementById('password');
      const btn = document.querySelector('.toggle-eye');
      const icon = btn.querySelector('i');
      btn.addEventListener('click', () => {
        const isPw = pwd.type === 'password';
        pwd.type = isPw ? 'text' : 'password';
        icon.className = isPw ? 'bi bi-eye-slash' : 'bi bi-eye';
        btn.setAttribute('aria-label', isPw ? 'Masquer le mot de passe' : 'Afficher le mot de passe');
      });
    })();

    // Désactiver le bouton à la soumission pour éviter les doubles clics
    (function(){
      const form = document.getElementById('loginForm');
      const submit = document.getElementById('submitBtn');
      form.addEventListener('submit', () => {
        submit.disabled = true;
        submit.innerHTML = '<i class="bi bi-arrow-repeat"></i> Connexion…';
      });
    })();
  </script>
</body>
</html>
