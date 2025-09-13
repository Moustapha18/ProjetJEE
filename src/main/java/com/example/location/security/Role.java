package com.example.location.security;

public enum Role {
    ADMIN,
    PROPRIETAIRE,
    LOCATAIRE, USER;


    public boolean isAdmin() { return this == ADMIN; }
    public boolean isProprietaire() { return this == PROPRIETAIRE; }
    public boolean isLocataire() { return this == LOCATAIRE; }
}
