package com.example.location.dto;

import com.example.location.security.Role;

public record UtilisateurDto(Long id, String username, String email, Role role) {}
