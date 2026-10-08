package com.tutorial.userService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResult {
    private boolean valid;
    private String email;
    private String error;
}