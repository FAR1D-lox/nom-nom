package com.nomnom.authorization_service.repository;

import com.nomnom.authorization_service.entity.AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorizationRepository extends JpaRepository<AuthorizationEntity, Long> {
    Optional<AuthorizationEntity> findByUsername(String username);
}
