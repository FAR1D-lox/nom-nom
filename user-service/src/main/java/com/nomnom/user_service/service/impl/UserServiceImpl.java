package com.nomnom.user_service.service.impl;

import com.nomnom.user_service.UserRepository;
import com.nomnom.user_service.dto.EditUserProfileDto;
import com.nomnom.user_service.dto.UserProfileDto;
import com.nomnom.user_service.entity.UserEntity;
import com.nomnom.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserProfileDto getProfile(Long userId) {
        UserEntity user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + userId + " not found"));
        return new UserProfileDto(
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt());
    }

    @Override
    public UserProfileDto editProfile(Long userId, EditUserProfileDto edit) {
        return null;
    }

    @Override
    public List<UserProfileDto> getSubscribers(Long userId) {
        return List.of();
    }

    @Override
    public List<UserProfileDto> getSubscriptions(Long userId) {
        return List.of();
    }

    @Override
    public void addSubscription(Long myId, Long otherId) {

    }

    @Override
    public void removeSubscription(Long myId, Long otherId) {

    }
}
