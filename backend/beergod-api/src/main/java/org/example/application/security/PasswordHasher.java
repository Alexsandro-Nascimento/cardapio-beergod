package org.example.application.security;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
    boolean isHashed(String password);
}