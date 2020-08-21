package com.example.springbootmonolith.service;

import com.example.springbootmonolith.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{

    public User getUser(String username);

    public Iterable<User> listUsers();

    public String createUser(User newUser);

    public String login(User user);

    public HttpStatus deleteById(Long userId);

    public User addCourse(String username, int courseId);
}