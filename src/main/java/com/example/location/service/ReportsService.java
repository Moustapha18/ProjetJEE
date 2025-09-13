package com.example.location.service;

import com.example.location.entity.Paiement;
import com.example.location.entity.PaiementStatut;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportsService {
    BigDecimal totalEncaisse(LocalDateTime fromTs, LocalDateTime toTs);
    BigDecimal totalAttendu(LocalDate from, LocalDate to);
    List<Paiement> paiementsBetween(LocalDate from, LocalDate to, PaiementStatut statutOrNull);
}
