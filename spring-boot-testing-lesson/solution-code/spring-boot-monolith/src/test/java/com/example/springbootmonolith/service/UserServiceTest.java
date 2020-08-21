package com.example.springbootmonolith.service;

import com.example.springbootmonolith.config.JwtUtil;
import com.example.springbootmonolith.model.User;
import com.example.springbootmonolith.repository.CourseRepository;
import com.example.springbootmonolith.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private User user;

    @Before
    public void initializeDummyUser(){
        user.setUsername("batman");
        user.setPassword("robin");
    }

    @Test
    public void getUser_ReturnsUser_Success(){

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        User tempUser = userService.getUser(user.getUsername());

        assertEquals(tempUser.getUsername(), user.getUsername());
    }

    @Test
    public void login_UserNotFound_Error(){

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        String token = userService.login(user);

        assertEquals(token, null);
    }
}