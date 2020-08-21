package com.quinto.ga.usersapi.repositories;

import com.quinto.ga.usersapi.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    public List<User> findByFirstNameContainsOrLastNameContains(String firstName, String lastName);
}
