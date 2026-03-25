package com.civicpulse.dto;

public class LoginResponse {
    private String token;
    private Long id;
    private String name;
    private String email;
    private String citizenId;

    public LoginResponse(String token, Long id, String name, String email, String citizenId) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.citizenId = citizenId;
    }

    // Getters
    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCitizenId() { return citizenId; }
}
