package com.nomnom.user_service;

import com.nomnom.UserRegisteredEvent;
import com.nomnom.UserRole;
import com.nomnom.user_service.entity.SubscribeEntity;
import com.nomnom.user_service.entity.UserEntity;
import com.nomnom.user_service.exception.PrivateProfileException;
import com.nomnom.user_service.exception.SubscriptionExistsException;
import com.nomnom.user_service.exception.SubscriptionNotFoundException;
import com.nomnom.user_service.exception.UsernameTakenException;
import com.nomnom.user_service.mapper.EditUserProfileDto;
import com.nomnom.user_service.mapper.UserMapper;
import com.nomnom.user_service.mapper.UserProfileDto;
import com.nomnom.user_service.repository.SubscribeRepository;
import com.nomnom.user_service.repository.UserRepository;
import com.nomnom.user_service.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscribeRepository subscribeRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity user1;
    private UserEntity user2;
    private UserProfileDto profileDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        user1 = UserEntity.builder()
                .id(1L)
                .username("user1")
                .role(UserRole.DEFAULT)
                .subscribeVision(SubscribeVision.VISIBLE)
                .createdAt(now)
                .build();
        user2 = UserEntity.builder()
                .id(1L)
                .username("user2")
                .role(UserRole.DEFAULT)
                .subscribeVision(SubscribeVision.INVISIBLE)
                .createdAt(now)
                .build();
        profileDto = new UserProfileDto(1L,
                "user1",
                UserRole.DEFAULT, now,
                0L,
                0L,
                SubscribeVision.VISIBLE);
    }

    @Test
    void getProfile_ShouldReturnProfile_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userMapper.toUserProfileDto(user1)).thenReturn(profileDto);

        UserProfileDto result = userService.getProfile(1L);

        assertNotNull(result);
        assertEquals(profileDto, result);
        verify(userRepository).findById(1L);
        verify(userMapper).toUserProfileDto(user1);
    }

    @Test
    void getProfile_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getProfile(99L));
        verify(userRepository).findById(99L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void editProfile_ShouldThrowUsernameTakenException_WhenUsernameChangedToExists() {
        EditUserProfileDto editProfileDto = new EditUserProfileDto("TakenName", UserRole.DEFAULT, SubscribeVision.VISIBLE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("TakenName")).thenReturn(true);

        assertThrows(UsernameTakenException.class, () -> userService.editProfile(1L, editProfileDto, false));
        verify(userRepository).existsByUsername("TakenName");
        verifyNoInteractions(userMapper);
    }

    @Test
    void editProfile_ShouldThrowAccessDeniedException_WhenUserTriesToChangeRoleWithoutAllPermission() {
        EditUserProfileDto editProfileDto = new EditUserProfileDto("NewName", UserRole.ADMIN, SubscribeVision.VISIBLE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertThrows(AccessDeniedException.class, () -> userService.editProfile(1L, editProfileDto, false));
    }

    @Test
    void editProfile_ShouldUpdateProfile_WhenSelfEditSuccess() {
        EditUserProfileDto editProfileDto = new EditUserProfileDto("NewName", UserRole.DEFAULT, SubscribeVision.INVISIBLE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("NewName")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user1);
        when(userMapper.toUserProfileDto(user1)).thenReturn(profileDto);

        assertNotNull(userService.editProfile(1L, editProfileDto, false));
        verify(userRepository).save(any(UserEntity.class));

    }

    //


    @Test
    void editProfile_ShouldUpdateProfile_WhenAdminEditSuccessAndSameUsername() {
        EditUserProfileDto editDto = new EditUserProfileDto("user1", UserRole.ADMIN, SubscribeVision.VISIBLE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user1);
        when(userMapper.toUserProfileDto(user1)).thenReturn(profileDto);

        UserProfileDto result = userService.editProfile(1L, editDto, true);

        assertNotNull(result);
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void editProfile_WhenAdminTriesToChangeSubscribeVision_ShouldThrowAccessDeniedException() {
        EditUserProfileDto editDto = new EditUserProfileDto("user1", UserRole.DEFAULT, SubscribeVision.INVISIBLE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1)); // у user1 видимость VISIBLE

        assertThrows(AccessDeniedException.class, () -> userService.editProfile(1L, editDto, true));
    }

    @Test
    void editProfile_WhenAdminEditsProfileWithoutChangingVision_ShouldSuccess() {
        EditUserProfileDto editDto = new EditUserProfileDto("NewName", UserRole.ADMIN, SubscribeVision.VISIBLE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1)); // у user1 видимость VISIBLE
        when(userRepository.existsByUsername("NewName")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user1);
        when(userMapper.toUserProfileDto(any())).thenReturn(profileDto);

        UserProfileDto result = userService.editProfile(1L, editDto, true);

        assertNotNull(result);
        verify(userRepository).save(any());
    }

    @Test
    void editProfile_WhenUserChangesOwnSubscribeVision_ShouldSuccess() {
        EditUserProfileDto editDto = new EditUserProfileDto("user1", UserRole.DEFAULT, SubscribeVision.INVISIBLE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenReturn(user1);
        when(userMapper.toUserProfileDto(any())).thenReturn(profileDto);

        UserProfileDto result = userService.editProfile(1L, editDto, false);

        assertNotNull(result);
        verify(userRepository).save(any());
    }

    @Test
    void getAllProfiles_ShouldReturnListOfProfiles() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toUserProfileDto(user1)).thenReturn(profileDto);

        List<UserProfileDto> result = userService.getAllProfiles();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getSubscribers_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.getSubscribers(1L, 10, 0));
    }

    @Test
    void getSubscribers_ShouldUseDefaultPaging_WhenPagingParamsAreNull() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(subscribeRepository.findSubscribersById(eq(1L), any(Pageable.class))).thenReturn(List.of(user2));
        when(userMapper.toUserProfileDto(user2)).thenReturn(profileDto);

        List<UserProfileDto> result = userService.getSubscribers(1L, null, null);

        assertEquals(1, result.size());
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(subscribeRepository).findSubscribersById(eq(1L), pageableCaptor.capture());
        assertEquals(50, pageableCaptor.getValue().getPageSize());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getSubscribers_ShouldUseCustomPaging_WhenPagingParamsAreProvided() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(subscribeRepository.findSubscribersById(eq(1L), any(Pageable.class))).thenReturn(List.of(user2));

        userService.getSubscribers(1L, 10, 2);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(subscribeRepository).findSubscribersById(eq(1L), pageableCaptor.capture());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
        assertEquals(2, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getSubscriptions_ShouldThrowEntityNotFoundException_WhenCheckedUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getSubscriptions(1L, 2L, 10, 0));
    }

    @Test
    void getSubscriptions_ShouldThrowPrivateProfileException_WhenCheckingOtherInvisibleProfile() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        assertThrows(PrivateProfileException.class, () -> userService.getSubscriptions(1L, 2L, 10, 0));
    }

    @Test
    void getSubscriptions_ShouldReturnSubscriptions_WhenCheckingOwnInvisibleProfile() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2)); // checkingId == checkedId == 2
        when(subscribeRepository.findSubscriptionsById(eq(2L), any(Pageable.class))).thenReturn(List.of(user1));

        List<UserProfileDto> result = userService.getSubscriptions(2L, 2L, null, null);

        assertNotNull(result);
        verify(subscribeRepository).findSubscriptionsById(eq(2L), any(Pageable.class));
    }

    @Test
    void getSubscriptions_ShouldReturnSubscriptions_WhenCheckingVisibleProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1)); // user1 is VISIBLE
        when(subscribeRepository.findSubscriptionsById(eq(1L), any(Pageable.class))).thenReturn(List.of(user2));

        List<UserProfileDto> result = userService.getSubscriptions(2L, 1L, 15, 1);

        assertNotNull(result);
        verify(subscribeRepository).findSubscriptionsById(eq(1L), any(Pageable.class));
    }

    @Test
    void addSubscription_ShouldThrowEntityNotFoundException_WhenMyUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.addSubscription(1L, 2L));
    }

    @Test
    void addSubscription_ShouldThrowEntityNotFoundException_WhenOtherUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.addSubscription(1L, 2L));
    }

    @Test
    void addSubscription_ShouldThrowSubscriptionExistsException_WhenSubscriptionAlreadyExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(subscribeRepository.existsBySubscriberIdAndSubscriptionId(1L, 2L)).thenReturn(true);

        assertThrows(SubscriptionExistsException.class, () -> userService.addSubscription(1L, 2L));
    }

    @Test
    void addSubscription_ShouldThrowIllegalArgumentException_WhenSubscribingToSelf() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(subscribeRepository.existsBySubscriberIdAndSubscriptionId(1L, 1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.addSubscription(1L, 1L));
    }

    @Test
    void addSubscription_ShouldSaveSubscriptionAndIncrementCounts_WhenValid() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(subscribeRepository.existsBySubscriberIdAndSubscriptionId(1L, 2L)).thenReturn(false);

        userService.addSubscription(1L, 2L);

        verify(userRepository).incrementSubscribersCount(2L);
        verify(userRepository).incrementSubscriptionsCount(1L);
        verify(subscribeRepository).save(any(SubscribeEntity.class));
    }

    @Test
    void removeSubscription_ShouldThrowEntityNotFoundException_WhenMyUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.removeSubscription(1L, 2L));
    }

    @Test
    void removeSubscription_ShouldThrowEntityNotFoundException_WhenOtherUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.removeSubscription(1L, 2L));
    }

    @Test
    void removeSubscription_ShouldThrowSubscriptionNotFoundException_WhenSubscriptionDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(subscribeRepository.existsBySubscriberIdAndSubscriptionId(1L, 2L)).thenReturn(false);

        assertThrows(SubscriptionNotFoundException.class, () -> userService.removeSubscription(1L, 2L));
    }

    @Test
    void removeSubscription_ShouldDeleteSubscriptionAndDecrementCounts_WhenValid() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(subscribeRepository.existsBySubscriberIdAndSubscriptionId(1L, 2L)).thenReturn(true);

        userService.removeSubscription(1L, 2L);

        verify(userRepository).decrementSubscribersCount(2L);
        verify(userRepository).decrementSubscriptionsCount(1L);
        verify(subscribeRepository).deleteBySubscriberIdAndSubscriptionId(1L, 2L);
    }

    @Test
    void createUserProfile_ShouldSaveNewUserEntity() {
        UserRegisteredEvent event = new UserRegisteredEvent(10L, "NewUser", UserRole.DEFAULT);

        userService.createUserProfile(event);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();

        assertEquals(10L, savedUser.getId());
        assertEquals("NewUser", savedUser.getUsername());
        assertEquals(UserRole.DEFAULT, savedUser.getRole());
        assertEquals(SubscribeVision.VISIBLE, savedUser.getSubscribeVision());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }


}
