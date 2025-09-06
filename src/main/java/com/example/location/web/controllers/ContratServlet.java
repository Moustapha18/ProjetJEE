package com.example.location.web.controllers;

import com.example.location.entity.*;
import com.example.location.service.AppartementService;
import com.example.location.service.LocataireService;
import com.example.location.service.ContratService;
import com.example.location.service.impl.AppartementServiceImpl;
import com.example.location.service.impl.LocataireServiceImpl;
import com.example.location.service.impl.ContratServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@WebServlet(name = "ContratServlet", urlPatterns = {"/app/contrats"})
public class ContratServlet extends HttpServlet {

    private final ContratService contratService = new ContratServiceImpl();
    private final AppartementService appartService = new AppartementServiceImpl();
    private final LocataireService locataireService = new LocataireServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = p(req.getParameter("action"));
        if (action.isEmpty() || action.equals("list")) {
            req.setAttribute("contrats", contratService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/contrat/list.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "new" -> {
                req.setAttribute("mode", "create");
                req.setAttribute("appartements", appartService.findAll());
                req.setAttribute("locataires", locataireService.findAll());
                req.setAttribute("statuts", StatutContrat.values());
                req.getRequestDispatcher("/WEB-INF/views/contrat/form.jsp").forward(req, resp);
            }
            case "edit" -> {
                Long id = parseId(req.getParameter("id"));
                contratService.findById(id).ifPresent(c -> req.setAttribute("contrat", c));
                req.setAttribute("mode", "edit");
                req.setAttribute("appartements", appartService.findAll());
                req.setAttribute("locataires", locataireService.findAll());
                req.setAttribute("statuts", StatutContrat.values());
                req.getRequestDispatcher("/WEB-INF/views/contrat/form.jsp").forward(req, resp);
            }
            case "delete" -> {
                Long id = parseId(req.getParameter("id"));
                contratService.deleteById(id);
                resp.sendRedirect(req.getContextPath() + "/app/contrats");
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = p(req.getParameter("action"));

        Long appartId = lv(req.getParameter("appartementId"));
        Long locId    = lv(req.getParameter("locataireId"));
        LocalDate dDeb = ld(req.getParameter("dateDebut"));
        LocalDate dFin = ld(req.getParameter("dateFin"));
        BigDecimal loyer = bd(req.getParameter("loyerMensuel"));
        BigDecimal caution = bd(req.getParameter("caution"));
        StatutContrat statut = sv(req.getParameter("statut"));

        if (appartId == null || locId == null || dDeb == null || loyer == null || statut == null) {
            req.setAttribute("error", "Champs obligatoires manquants.");
            req.setAttribute("mode", action.equals("update") ? "edit" : "create");
            req.setAttribute("appartements", appartService.findAll());
            req.setAttribute("locataires", locataireService.findAll());
            req.setAttribute("statuts", StatutContrat.values());
            if (action.equals("update")) {
                Long id = parseId(req.getParameter("id"));
                contratService.findById(id).ifPresent(c -> req.setAttribute("contrat", c));
            }
            req.getRequestDispatcher("/WEB-INF/views/contrat/form.jsp").forward(req, resp);
            return;
        }


        Appartement appart = appartService.findById(appartId)
                .orElseThrow(() -> new IllegalArgumentException("Appartement introuvable"));
        Locataire locataire = locataireService.findById(locId)
                .orElseThrow(() -> new IllegalArgumentException("Locataire introuvable"));

        Contrat data = new Contrat(null, appart, locataire, dDeb, dFin, loyer, caution, statut);

        if (action.equals("update")) {
            Long id = parseId(req.getParameter("id"));
            contratService.update(id, data);
        } else {
            contratService.save(data);
        }
        resp.sendRedirect(req.getContextPath() + "/app/contrats");
    }


    private String p(String s){ return s==null? "": s.trim(); }
    private Long parseId(String s){ try { return Long.valueOf(s); } catch(Exception e){ throw new IllegalArgumentException("id invalide"); } }
    private Long lv(String s){ try { return s==null||s.isBlank()? null : Long.valueOf(s.trim()); } catch(Exception e){ return null; } }
    private java.math.BigDecimal bd(String s){ try { return s==null||s.isBlank()? null : new java.math.BigDecimal(s.trim()); } catch(Exception e){ return null; } }
    private java.time.LocalDate ld(String s){ try { return s==null||s.isBlank()? null : java.time.LocalDate.parse(s.trim()); } catch(Exception e){ return null; } }
    private StatutContrat sv(String s){ try { return s==null||s.isBlank()? null : StatutContrat.valueOf(s.trim()); } catch(Exception e){ return null; } }
}
