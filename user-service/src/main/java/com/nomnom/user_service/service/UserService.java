package com.nomnom.user_service.service;

import com.nomnom.UserRegisteredEvent;
import com.nomnom.user_service.mapper.RequestEditUserProfileDto;
import com.nomnom.user_service.mapper.ResponseUserProfileDto;

import java.util.List;

public interface UserService {
    ResponseUserProfileDto getProfile(Long userId);

    ResponseUserProfileDto editProfile(Long userId, RequestEditUserProfileDto edit, boolean haveAllPermission);

    List<ResponseUserProfileDto> getSubscribers(Long userId, Integer pageSize, Integer pageNumber);

    List<ResponseUserProfileDto> getSubscriptions(Long checkerId, Long checkedId, Integer pageSize, Integer pageNumber);

    void addSubscription(Long myId, Long otherId);

    void removeSubscription(Long myId, Long otherId);

    void createUserProfile(UserRegisteredEvent event);

    List<ResponseUserProfileDto> getAllProfiles();
}
