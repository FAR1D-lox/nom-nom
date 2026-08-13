package com.nomnom.authorization_service.service;

import com.nomnom.authorization_service.entity.AuthorizationEntity;
import com.nomnom.authorization_service.entity.LoginRequest;
import com.nomnom.authorization_service.entity.LoginResponse;
import com.nomnom.authorization_service.entity.RegisterRequest;

public interface AuthorizationService {

    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    boolean validate(String token);

    AuthorizationEntity getUserByUsername(String username);

}
