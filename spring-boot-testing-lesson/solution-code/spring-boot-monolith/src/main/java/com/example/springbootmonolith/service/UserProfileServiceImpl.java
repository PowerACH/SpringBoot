package com.example.springbootmonolith.service;

import com.example.springbootmonolith.model.User;
import com.example.springbootmonolith.model.UserProfile;
import com.example.springbootmonolith.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private UserProfileRepository userProfileRepository;

    private UserService userService;

    @Autowired
    public UserProfileServiceImpl(UserService userService, UserProfileRepository userProfileRepository){
        this.userService = userService;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile createUserProfile(String username, UserProfile newProfile) {
        User user = userService.getUser(username);
        newProfile.setUser(user);
        return userProfileRepository.save(newProfile);
    }

    @Override
    public UserProfile getUserProfile(String username) {
        return userProfileRepository.findProfileByUsername(username);
    }
}
