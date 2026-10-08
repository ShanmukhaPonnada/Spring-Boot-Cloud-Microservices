package com.tutorial.userService.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tutorial.userService.model.User;
import com.tutorial.userService.producer.UserEventProducer;
import com.tutorial.userService.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("User Service is running!");
    }

    private final UserService userService;
    private final UserEventProducer userEventProducer;

    @GetMapping
    public List<User> getAllUsers() {
        userEventProducer.sendUserCreatedEvent(new Long("10"), "hacker", "hacker");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        userEventProducer.sendUserCreatedEvent(createdUser.getId(),
                createdUser.getFirstName() + " " + createdUser.getLastName(), createdUser.getEmail());
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        userEventProducer.sendUserUpdatedEvent(updatedUser.getId(),
                updatedUser.getFirstName() + " " + updatedUser.getLastName(), updatedUser.getEmail());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        userEventProducer.sendUserDeletedEvent(id);
        return ResponseEntity.noContent().build();
    }
}
