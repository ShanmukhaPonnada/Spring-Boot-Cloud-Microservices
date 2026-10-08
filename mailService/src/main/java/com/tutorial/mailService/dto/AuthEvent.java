package com.tutorial.mailService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthEvent {
    private String eventType;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long timestamp;
}