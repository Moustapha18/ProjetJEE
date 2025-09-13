package com.example.location.security;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {

    public static class TestPassword {
        public static void main(String[] args) {
            String hash = "$2a$12$2wOrCMZs4xxUjqRLsmEmROUN31aaSLpjFhh.bZOGCbJz8veV9pTSe";
            String raw = "admin2"; // ou "password" selon ce que tu as utilisé

            boolean result = PasswordHasher.matches(raw, hash);
            System.out.println("Mot de passe valide ? " + result);
        }
    }

    private PasswordHasher() {}
    public static String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt(12)); }
    public static boolean matches(String raw, String hash) { return BCrypt.checkpw(raw, hash); }
}
