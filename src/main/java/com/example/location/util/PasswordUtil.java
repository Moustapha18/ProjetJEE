//package com.example.location.util;
//
//import org.mindrot.jbcrypt.BCrypt;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.util.Objects;
//
//public final class PasswordUtil {
//    private PasswordUtil(){}
//
//    /** Utilisé pour les NOUVEAUX comptes (on standardise sur BCrypt) */
//    public static String store(String raw) {
//        return BCrypt.hashpw(raw, BCrypt.gensalt(12));
//    }
//
//    /** Vérifie le mot de passe contre la valeur stockée (BCrypt, SHA-256 hex, ou clair) */
//    public static boolean matches(String raw, String stored) {
//        if (stored == null) return false;
//        stored = stored.trim();
//
//        // 1) BCrypt
//        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
//            return BCrypt.checkpw(raw, stored);
//        }
//        // 2) SHA-256 en hex (64 caractères)
//        if (stored.matches("^[0-9a-fA-F]{64}$")) {
//            return sha256(raw).equalsIgnoreCase(stored);
//        }
//        // 3) fallback: égalité simple (si tu as eu du "mot de passe en clair" en dev)
//        return Objects.equals(raw, stored);
//    }
//
//    private static String sha256(String raw) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));
//            StringBuilder sb = new StringBuilder();
//            for (byte b : bytes) sb.append(String.format("%02x", b));
//            return sb.toString();
//        } catch (Exception e) {
//            throw new RuntimeException("Hash error", e);
//        }
//    }
//}
