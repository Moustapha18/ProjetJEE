package com.example.location.dto;

/** DTO: sépare la vue des entités (ISP/DIP) */
public record ImmeubleDto(Long id, String nom, String adresse) {}
