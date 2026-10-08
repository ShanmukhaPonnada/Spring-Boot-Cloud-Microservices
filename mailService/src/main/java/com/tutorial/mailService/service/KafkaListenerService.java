package com.tutorial.mailService.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tutorial.mailService.dto.AuthEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final EmailService emailService;

    @KafkaListener(topics = "${app.kafka.topics.auth-events:auth-events}", groupId = "mail-group-id")
    public void processAuthMessages(AuthEvent data) {
        emailService.sendSimpleEmail(data.getUserEmail(), "Auth Event Received  ", "Hello " + data.getUserName()
                + ",\n\nWe received an authentication event for your account at " + data.getTimestamp()
                + ".\n\nEvent Type: " + data.getEventType()
                + "\n\nIf this was you, no further action is needed. If you did not initiate this event, please secure your account immediately.\n\nBest regards,\nWigothehacker Team");
    }

}
