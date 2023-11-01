package com.example.models.repo;

import com.example.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByLogin(String login);
    Optional<User> findByLogin(String login);
    List<User> findByLoginContainsIgnoreCase(String login);
}
