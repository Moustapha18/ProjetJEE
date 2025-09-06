package com.example.location.security;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {
    private PasswordHasher() {}
    public static String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt(12)); }
    public static boolean matches(String raw, String hash) { return BCrypt.checkpw(raw, hash); }
}
