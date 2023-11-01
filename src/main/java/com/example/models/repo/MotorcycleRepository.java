package com.example.models.repo;

import com.example.models.entity.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotorcycleRepository extends JpaRepository<Motorcycle, Integer> {
    List<Motorcycle> findByModelContainsIgnoreCase(String model);
}
