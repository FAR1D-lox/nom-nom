package com.nomnom.authorization_service.service;

public interface JwtService {

    String generateToken(String username, String role);

    String extractUsername(String token);

    String extractRole(String token);

    boolean validateToken(String token);

}
