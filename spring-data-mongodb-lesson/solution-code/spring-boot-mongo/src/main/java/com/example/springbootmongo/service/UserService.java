package com.example.springbootmongo.service;

import com.example.springbootmongo.model.User;

import java.util.List;

public interface UserService {

    public List<User> listUsers();

    public User createUser(User newUser);

    public User findUserByEmail(String email);

}