package com.nomnom.user_service.service;

import com.nomnom.UserRegisteredEvent;
import com.nomnom.user_service.mapper.EditUserProfileDto;
import com.nomnom.user_service.mapper.UserProfileDto;

import java.util.List;

public interface UserService {
    UserProfileDto getProfile(Long userId);

    UserProfileDto editProfile(Long userId, EditUserProfileDto edit, boolean haveAllPermission);

    List<UserProfileDto> getSubscribers(Long userId, Integer pageSize, Integer pageNumber);

    List<UserProfileDto> getSubscriptions(Long checkerId, Long checkedId, Integer pageSize, Integer pageNumber);

    void addSubscription(Long myId, Long otherId);

    void removeSubscription(Long myId, Long otherId);

    void createUserProfile(UserRegisteredEvent event);

    List<UserProfileDto> getAllProfiles();
}
