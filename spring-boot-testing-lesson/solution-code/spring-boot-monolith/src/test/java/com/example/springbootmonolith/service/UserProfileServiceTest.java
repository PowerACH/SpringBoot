package com.example.springbootmonolith.service;

import com.example.springbootmonolith.model.UserProfile;
import com.example.springbootmonolith.repository.UserProfileRepositoryStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserProfileServiceTest {

    private UserProfileServiceImpl userProfileService;

    @Before
    public void initializeUserProfile(){
        userProfileService = new UserProfileServiceImpl(new UserServiceStub(), new UserProfileRepositoryStub());
    }

    @Test
    public void createUserProfile_AddsProfile_Success(){

        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("batman@superhero.com");

        UserProfile newProfile = userProfileService.createUserProfile("batman", userProfile);

        Assert.assertNotNull(newProfile);
        Assert.assertEquals(newProfile.getEmail(), userProfile.getEmail());
    }

    @Test
    public void getUserProfile_RetrievesProfileByUsername_Success(){

    }
}
