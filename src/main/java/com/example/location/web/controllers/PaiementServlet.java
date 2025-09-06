package com.example.location.web.controllers;

import com.example.location.entity.*;
import com.example.location.service.PaiementService;
import com.example.location.service.impl.PaiementServiceImpl;
import com.example.location.service.ContratService;
import com.example.location.service.impl.ContratServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "PaiementServlet", urlPatterns = {"/app/paiements", "/app/paiement"})
public class PaiementServlet extends HttpServlet {

    private final PaiementService service = new PaiementServiceImpl();
    private final ContratService contratService = new ContratServiceImpl();

    private String t(String s){ return s == null ? "" : s.trim(); }
    private Long asLong(String s){ try { return (s==null||s.isBlank())? null : Long.valueOf(s.trim()); } catch(Exception e){ return null; } }
    private BigDecimal asBd(String s){ try { return (s==null||s.isBlank())? null : new BigDecimal(s.trim()); } catch(Exception e){ return null; } }
    private LocalDate asDate(String s){ try { return (s==null||s.isBlank())? null : LocalDate.parse(s.trim()); } catch(Exception e){ return null; } }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = t(req.getParameter("action"));

        if (action.isEmpty() || action.equals("list")) {
            req.setAttribute("paiements", service.findAll());
            req.getRequestDispatcher("/WEB-INF/views/paiement/list.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "new" -> {
                fillRefs(req);
                req.setAttribute("mode", "create");
                req.getRequestDispatcher("/WEB-INF/views/paiement/form.jsp").forward(req, resp);
            }
            case "edit" -> {
                Long id = asLong(req.getParameter("id"));
                service.findById(id).ifPresent(p -> req.setAttribute("paiement", p));
                fillRefs(req);
                req.setAttribute("mode", "edit");
                req.getRequestDispatcher("/WEB-INF/views/paiement/form.jsp").forward(req, resp);
            }
            case "delete" -> {
                Long id = asLong(req.getParameter("id"));
                service.deleteById(id);
                resp.sendRedirect(req.getContextPath() + "/app/paiements");
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void fillRefs(HttpServletRequest req) {
        List<Contrat> contrats = contratService.findAll(); // utilisé pour le <select>
        req.setAttribute("contrats", contrats);
        req.setAttribute("modes", ModePaiement.values());
        req.setAttribute("statuts", StatutPaiement.values());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = t(req.getParameter("action"));

        Long contratId = asLong(req.getParameter("contratId"));
        LocalDate datePaiement = asDate(req.getParameter("datePaiement"));
        BigDecimal montant = asBd(req.getParameter("montant"));
        String modeStr = t(req.getParameter("mode"));
        String reference = t(req.getParameter("reference"));
        String statutStr = t(req.getParameter("statut"));

        if (contratId == null || datePaiement == null || montant == null || montant.signum() < 0 || modeStr.isBlank() || statutStr.isBlank()) {
            req.setAttribute("error", "Veuillez renseigner les champs obligatoires (contrat, date, montant, mode, statut).");
            fillRefs(req);
            req.setAttribute("mode", action.equals("update") ? "edit" : "create");
            if (action.equals("update")) {
                Long id = asLong(req.getParameter("id"));
                service.findById(id).ifPresent(p -> req.setAttribute("paiement", p));
            }
            req.getRequestDispatcher("/WEB-INF/views/paiement/form.jsp").forward(req, resp);
            return;
        }

        // reconstruire l’objet minimal Contrat pour setter la FK
        Contrat c = new Contrat();
        c.setId(contratId);

        Paiement payload = new Paiement(
                null, c, datePaiement, montant,
                ModePaiement.valueOf(modeStr), reference.isEmpty()? null : reference,
                StatutPaiement.valueOf(statutStr)
        );

        if (action.equals("update")) {
            Long id = asLong(req.getParameter("id"));
            service.update(id, payload);
        } else {
            service.save(payload);
        }
        resp.sendRedirect(req.getContextPath() + "/app/paiements");
    }
}
