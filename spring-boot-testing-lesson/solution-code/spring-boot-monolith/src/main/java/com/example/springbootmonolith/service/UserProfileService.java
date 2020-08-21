package com.example.springbootmonolith.service;

import com.example.springbootmonolith.model.UserProfile;

public interface UserProfileService {

    public UserProfile createUserProfile(String username, UserProfile newProfile);

    public UserProfile getUserProfile(String username);
}