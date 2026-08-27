package com.nomnom.user_service.kafka;

import com.nomnom.UserRegisteredEvent;
import com.nomnom.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserKafkaListener {

    private final UserService service;

    @KafkaListener(topics = "user-registered-topic", groupId = "user-service-group")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for userId: {}", event.id());
        service.createUserProfile(event);
    }
}
