package com.example.location.util;

public final class MailUtil {
    private MailUtil(){}

    // Stub : remplace par JavaMail si besoin
    public static void send(String to, String subject, String body) {
        System.out.println("[MAIL] to=" + to + " | subject=" + subject + " | body=" + body);
    }
}
