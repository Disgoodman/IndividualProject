package com.example.models.repo;

import com.example.models.entity.Engine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngineRepository extends JpaRepository<Engine, Integer> {
    List<Engine> findByModelContainsIgnoreCase(String model);
}
