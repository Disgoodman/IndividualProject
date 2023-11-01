package com.example.models.repo;

import com.example.models.entity.EngineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngineTypeRepository extends JpaRepository<EngineType, Integer> {
    List<EngineType> findByNameContainsIgnoreCase(String name);
}
