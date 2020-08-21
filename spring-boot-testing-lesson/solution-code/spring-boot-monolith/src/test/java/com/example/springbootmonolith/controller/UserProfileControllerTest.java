package com.example.springbootmonolith.controller;

import com.example.springbootmonolith.model.UserProfile;
import com.example.springbootmonolith.service.UserProfileServiceStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserProfileControllerTest {

    private UserProfileController userProfileController;

    @Before
    public void initializeUserProfileController(){
        userProfileController = new UserProfileController();
        userProfileController.setUserProfileService(new UserProfileServiceStub());
    }

    @Test
    public void createUserProfile_SavesUserProfile_Success() throws Exception{
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("batman@superhero.com");

        UserProfile newProfile = userProfileController.createUserProfile("batman", userProfile);

        Assert.assertNotNull(newProfile);
        Assert.assertEquals(newProfile.getEmail(), userProfile.getEmail());
    }
}
