package com.nomnom.authorization_service.service;

public interface JwtService {

    String generateToken(Long userId, String role);

}
