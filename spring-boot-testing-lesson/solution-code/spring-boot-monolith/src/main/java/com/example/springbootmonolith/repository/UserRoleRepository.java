package com.example.springbootmonolith.repository;

import com.example.springbootmonolith.model.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {

    public UserRole findByName(String name);
}
