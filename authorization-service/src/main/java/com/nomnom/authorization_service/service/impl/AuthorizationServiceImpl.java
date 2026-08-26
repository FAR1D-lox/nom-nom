package com.nomnom.authorization_service.service.impl;

import com.nomnom.UserRole;
import com.nomnom.authorization_service.Exception.EntityAlreadyExistsException;
import com.nomnom.authorization_service.entity.AuthorizationEntity;
import com.nomnom.authorization_service.entity.LoginRequest;
import com.nomnom.authorization_service.entity.LoginResponse;
import com.nomnom.authorization_service.entity.RegisterRequest;
import com.nomnom.authorization_service.repository.AuthorizationRepository;
import com.nomnom.authorization_service.service.AuthorizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthorizationRepository repository;
    private final JwtServiceImpl jwtService;
    private final PasswordEncoder encoder;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.username()).isPresent()) {
            throw new EntityAlreadyExistsException("User with username " + request.username() + " already exists");
        }

        AuthorizationEntity user = new AuthorizationEntity();
        user.setUsername(request.username());
        user.setRole(request.role() != null ? request.role() : UserRole.DEFAULT);
        user.setPasswordHash(encoder.encode(request.password()));
        repository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new LoginResponse(token);
    }

    public LoginResponse login(LoginRequest request) {
        AuthorizationEntity user = repository.findByUsername(request.username())
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + request.username()));

        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Wrong password");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new LoginResponse(token);
    }

    public AuthorizationEntity getUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with userId: " + userId));
    }

}
