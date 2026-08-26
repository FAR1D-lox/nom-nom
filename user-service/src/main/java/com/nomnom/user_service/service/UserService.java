package com.nomnom.user_service.service;

import com.nomnom.user_service.dto.EditUserProfileDto;
import com.nomnom.user_service.dto.UserProfileDto;

import java.util.List;

public interface UserService {
    UserProfileDto getProfile(Long userId);

    UserProfileDto editProfile(Long userId, EditUserProfileDto edit);

    List<UserProfileDto> getSubscribers(Long userId);

    List<UserProfileDto> getSubscriptions(Long userId);

    void addSubscription(Long myId, Long otherId);

    void removeSubscription(Long myId, Long otherId);
}
