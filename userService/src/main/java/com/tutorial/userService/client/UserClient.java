package com.tutorial.userService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tutorial.userService.model.User;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "user-service")
public interface UserClient {

    @CircuitBreaker(name = "userServiceCircuitBreaker", fallbackMethod = "fallbackGetUserById")
    @GetMapping("/users/{id}")
    User getUserById(@PathVariable("id") Long id);

    default User fallbackGetUserById(Long id, Throwable throwable) {
        return new User();
    }
}
