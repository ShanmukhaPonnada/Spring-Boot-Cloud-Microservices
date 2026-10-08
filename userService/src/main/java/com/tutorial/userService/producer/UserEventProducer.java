package com.tutorial.userService.producer;

import com.tutorial.userService.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public void sendUserCreatedEvent(Long userId, String userName, String userEmail) {
        UserEvent event = new UserEvent(
            "USER_CREATED",
            userId,
            userName,
            userEmail,
            System.currentTimeMillis()
        );
        kafkaTemplate.send(TOPIC, userId.toString(), event);
        log.info("Sent USER_CREATED event for user: {}", userName);
    }

    public void sendUserUpdatedEvent(Long userId, String userName, String userEmail) {
        UserEvent event = new UserEvent(
            "USER_UPDATED",
            userId,
            userName,
            userEmail,
            System.currentTimeMillis()
        );
        kafkaTemplate.send(TOPIC, userId.toString(), event);
        log.info("Sent USER_UPDATED event for user: {}", userName);
    }

    public void sendUserDeletedEvent(Long userId) {
        UserEvent event = new UserEvent(
            "USER_DELETED",
            userId,
            null,
            null,
            System.currentTimeMillis()
        );
        kafkaTemplate.send(TOPIC, userId.toString(), event);
        log.info("Sent USER_DELETED event for user ID: {}", userId);
    }
}