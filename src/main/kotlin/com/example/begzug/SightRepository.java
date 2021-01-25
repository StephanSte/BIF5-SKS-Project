package com.example.begzug;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface SightRepository extends CrudRepository<Sight, Integer> {
    @NotNull
    List<Sight> findAll();
    List<Sight> findByName(String name);
}