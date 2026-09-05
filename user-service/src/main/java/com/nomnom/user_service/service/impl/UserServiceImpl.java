package com.nomnom.user_service.service.impl;

import com.nomnom.UserRegisteredEvent;
import com.nomnom.user_service.mapper.ResponseUserProfileDto;
import com.nomnom.user_service.mapper.UserMapper;
import com.nomnom.user_service.entity.SubscribeEntity;
import com.nomnom.user_service.exception.PrivateProfileException;
import com.nomnom.user_service.SubscribeVision;
import com.nomnom.user_service.exception.SubscriptionExistsException;
import com.nomnom.user_service.exception.SubscriptionNotFoundException;
import com.nomnom.user_service.repository.UserRepository;
import com.nomnom.user_service.exception.UsernameTakenException;
import com.nomnom.user_service.mapper.RequestEditUserProfileDto;
import com.nomnom.user_service.entity.UserEntity;
import com.nomnom.user_service.repository.SubscribeRepository;
import com.nomnom.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;
    private final UserMapper userMapper;

    @Override
    public ResponseUserProfileDto getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + userId + " not found"));
        return userMapper.toUserProfileDto(user);
    }

    @Transactional
    @Override
    public ResponseUserProfileDto editProfile(
            Long userId,
            RequestEditUserProfileDto edit,
            boolean haveAllPermission) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User with id = " + userId + " not found"));

        if (!user.getUsername().equals(edit.username()) && userRepository.existsByUsername(edit.username())) {
            throw new UsernameTakenException("Username " + edit.username() + " has already taken by someone");
        }

        if (!haveAllPermission && !user.getRole().equals(edit.role())) {
            throw new AccessDeniedException("You cannot change your role");
        }

        if (haveAllPermission && !user.getSubscribeVision().equals(edit.subscribeVision())) {
            throw new AccessDeniedException("You cannot infringe upon a user's freedom");
        }

        UserEntity editedUser = UserEntity
                .builder()
                .id(user.getId())
                .username(edit.username())
                .role(edit.role())
                .subscribersCount(user.getSubscribersCount())
                .subscribeVision(edit.subscribeVision())
                .subscriptionsCount(user.getSubscriptionsCount())
                .build();

        return userMapper.toUserProfileDto(userRepository.save(editedUser));
    }

    @Override
    public List<ResponseUserProfileDto> getAllProfiles() { //Удалить потом
        return userRepository.findAll().stream().map(userMapper::toUserProfileDto).toList();
    }

    @Override
    public List<ResponseUserProfileDto> getSubscribers(
            Long userId,
            Integer pageSize,
            Integer pageNumber
    ) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id = " + userId + " not found");
        }

        pageSize = pageSize != null ? pageSize : 50;
        pageNumber = pageNumber != null ? pageNumber : 0;
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<UserEntity> subscribers = subscribeRepository.findSubscribersById(userId, pageable);

        return subscribers.stream().map(userMapper::toUserProfileDto).toList();
    }

    @Override
    public List<ResponseUserProfileDto> getSubscriptions(
            Long checkingId,
            Long checkedId,
            Integer pageSize,
            Integer pageNumber
    ) {
        UserEntity user = userRepository.findById(checkedId)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + checkedId + " not found"));

        if (!checkingId.equals(checkedId) && SubscribeVision.INVISIBLE.equals(user.getSubscribeVision()))
            throw new PrivateProfileException(
                    "You don't have permission to see user's subscriptions with id = " + checkedId);

        pageSize = pageSize != null ? pageSize : 50;
        pageNumber = pageNumber != null ? pageNumber : 0;
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<UserEntity> subscriptions = subscribeRepository.findSubscriptionsById(checkedId, pageable);
        return subscriptions.stream().map(userMapper::toUserProfileDto).toList();
    }

    @Override
    @Transactional
    public void addSubscription(Long myId, Long otherId) {
        if (!userRepository.existsById(myId)) {
            throw new EntityNotFoundException("User with id = " + myId + " not found");
        }
        if (!userRepository.existsById(otherId)) {
            throw new EntityNotFoundException("User with id = " + otherId + " not found");
        }

        if (subscribeRepository.existsBySubscriberIdAndSubscriptionId(myId, otherId)) {
            throw new SubscriptionExistsException("Subscription from " + myId + " to " + otherId + " has already exists");
        }

        if (myId.equals(otherId))
            throw new IllegalArgumentException("User cannot subscribe to themselves");

        userRepository.incrementSubscribersCount(otherId);
        userRepository.incrementSubscriptionsCount(myId);
        SubscribeEntity subscription = SubscribeEntity
                .builder()
                .subscriberId(myId)
                .subscriptionId(otherId)
                .build();
        subscribeRepository.save(subscription);

    }

    @Override
    @Transactional
    public void removeSubscription(Long myId, Long otherId) {
        if (!userRepository.existsById(myId)) {
            throw new EntityNotFoundException("User with id = " + myId + " not found");
        }
        if (!userRepository.existsById(otherId)) {
            throw new EntityNotFoundException("User with id = " + otherId + " not found");
        }
        if (!subscribeRepository.existsBySubscriberIdAndSubscriptionId(myId, otherId))
            throw new SubscriptionNotFoundException("Subscription from " + myId + " to " + otherId + " not found");
        userRepository.decrementSubscribersCount(otherId);
        userRepository.decrementSubscriptionsCount(myId);
        subscribeRepository.deleteBySubscriberIdAndSubscriptionId(myId, otherId);
    }

    @Override
    @Transactional
    public void createUserProfile(UserRegisteredEvent event) {
        UserEntity user = UserEntity
                .builder()
                .id(event.id())
                .username(event.username())
                .role(event.role())
                .subscribeVision(SubscribeVision.VISIBLE)
                .build();
        userRepository.save(user);
    }
}
