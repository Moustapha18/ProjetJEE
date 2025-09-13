package com.example.location.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PaiementStatutConverter implements AttributeConverter<PaiementStatut, String> {

    @Override
    public String convertToDatabaseColumn(PaiementStatut attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case EN_ATTENTE -> "EN_ATTENTE";
            case PAYE       -> "PAYE";
            case RETARD     -> "RETARD";
        };
    }

    @Override
    public PaiementStatut convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String key = dbData.trim().toUpperCase();

        // tolérance aux anciennes valeurs ou typos
        if ("ENRETARD".equals(key) || "EN_RETARD".equals(key)) key = "RETARD";
        if ("ENATTENTE".equals(key)) key = "EN_ATTENTE";

        return switch (key) {
            case "EN_ATTENTE" -> PaiementStatut.EN_ATTENTE;
            case "PAYE"       -> PaiementStatut.PAYE;
            case "RETARD"     -> PaiementStatut.RETARD;
            default -> throw new IllegalArgumentException("Statut paiement inconnu: " + dbData);
        };
    }
}
