package org.hoseo.ictcloudspring.dto;

import java.util.UUID;

public class Token {
    private String email;
    private String token;

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public Token(String email) {
        this.email = email;
        this.token = generateToken();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
