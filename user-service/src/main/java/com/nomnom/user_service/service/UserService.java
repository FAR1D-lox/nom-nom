package com.nomnom.user_service;

public interface UserService {
    UserProfileDto getMyProfile(Long userId);

    UserProfileDto getOtherProfile(Long userId);

    UserProfileDto editProfile(Long userId, RequestUserEditDto edit);

    List<UserProfileDto> getSubscribers(Long userId);

    List<UserProfileDto> getSubscriptions(Long userId);

    return
}
