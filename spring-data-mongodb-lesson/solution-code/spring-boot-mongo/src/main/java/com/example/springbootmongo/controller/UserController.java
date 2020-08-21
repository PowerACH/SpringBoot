package com.example.springbootmongo.controller;

import com.example.springbootmongo.model.User;
import com.example.springbootmongo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/list")
    public List<User> listUsers(){
        return userService.listUsers();
    }

    @PostMapping("/signup")
    public User createUser(@RequestBody User newUser){
        return userService.createUser(newUser);
    }

    @GetMapping("/user/{email}")
    public User getUserByEmail(@PathVariable String email){
        return userService.findUserByEmail(email);
    }

    @GetMapping("/hello")
    public String helloWorld(){
        return "Hello World!!";
    }
}
