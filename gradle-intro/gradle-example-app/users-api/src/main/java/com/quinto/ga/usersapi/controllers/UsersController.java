package com.quinto.ga.usersapi.controllers;

import com.quinto.ga.usersapi.models.User;
import com.quinto.ga.usersapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsersController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/all")
    public Iterable<User> getAll() {
        return userRepo.findAll();
    }

    @GetMapping("/view/{id}")
    public User searchById(@PathVariable long id) {
        return userRepo.findOne(id);
    }

    @GetMapping("/search/{name}")
    public Iterable<User> searchByName(@PathVariable String name) {
        String normalized = name.trim().toLowerCase();
        return userRepo.findByFirstNameContainsOrLastNameContains(normalized, normalized);
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus deleteUser(@PathVariable long id) {
        userRepo.delete(id);
        return HttpStatus.OK;
    }

    @PostMapping("/create")
    public HttpStatus createUser(@RequestBody User user) {
        userRepo.save(user);
        return HttpStatus.OK;
    }

    @PatchMapping("/update/{id}")
    public HttpStatus updateUser(@PathVariable long id, @RequestBody User userRequest) {
        User user = userRepo.findOne(id);
        user.setUserName(userRequest.getUserName());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        userRepo.save(user);
        return HttpStatus.OK;
    }
}
