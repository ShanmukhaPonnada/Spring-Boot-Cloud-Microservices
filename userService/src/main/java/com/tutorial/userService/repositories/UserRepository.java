package com.tutorial.userService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tutorial.userService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
