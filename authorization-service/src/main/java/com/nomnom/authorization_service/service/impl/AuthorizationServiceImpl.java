package com.nomnom.authorization_service.service.impl;

import com.nomnom.UserRegisteredEvent;
import com.nomnom.UserRole;
import com.nomnom.authorization_service.Exception.EntityAlreadyExistsException;
import com.nomnom.authorization_service.entity.AuthorizationEntity;
import com.nomnom.authorization_service.dto.LoginRequest;
import com.nomnom.authorization_service.dto.LoginResponse;
import com.nomnom.authorization_service.dto.RegisterRequest;
import com.nomnom.authorization_service.repository.AuthorizationRepository;
import com.nomnom.authorization_service.service.AuthorizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthorizationRepository repository;
    private final JwtServiceImpl jwtService;
    private final PasswordEncoder encoder;

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    private static final String USER_REGISTERED_TOPIC = "user-registered-topic";

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.username()).isPresent()) {
            throw new EntityAlreadyExistsException("User with username " + request.username() + " already exists");
        }

        AuthorizationEntity user = new AuthorizationEntity();
        user.setUsername(request.username());
        user.setRole(request.role() != null ? request.role() : UserRole.DEFAULT);
        user.setPasswordHash(encoder.encode(request.password()));
        AuthorizationEntity savedUser = repository.save(user);

        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole()
        );
        log.info("UserRegisterEvent has created");
        kafkaTemplate.send(USER_REGISTERED_TOPIC, String.valueOf(savedUser.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error send into Kafka");
                    } else {
                        log.info("UserRegisterEvent has send to topic: {}, offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()
                        );
                    }
                });

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
