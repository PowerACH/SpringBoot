## Integration Testing
Integration testing is when you integrate 2 or more units or modules or sub-systems or systems. In the real world the term integration testing is used with 2 meaning, either integration of modules, or systems.

Integration tests help us build confidence but they come at a price:

- That is a slower execution speed, which means slower builds
- Also, integration tests imply a broader testing scope which is not ideal in most cases

**Note:** It is recommended that test methods be written so that they are independent of the order that they are executed.

Add dependencies in `pom.xml`,

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-test</artifactId>
	<scope>test</scope>
</dependency>
		
<dependency>
	<groupId>com.h2database</groupId>
	<artifactId>h2</artifactId>
	<version>1.4.200</version>
</dependency>
```

Create properties file `application-qa.properties` for test under `/resources`

```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Enabling H2 Console
spring.h2.console.enabled=true

# Custom H2 Console URL
spring.h2.console.path=/h2
```

Create a package `com.example.springbootmonolith.integration` under `/test` folder. Under this package create a new `UserIntegrationTest` class.

```
package com.example.springbootmonolith.integration;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.springbootmonolith.repository.UserRoleRepository;
import com.example.springbootmonolith.model.User;
import com.example.springbootmonolith.model.UserRole;

@ActiveProfiles("qa")
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserIntegrationTest {

	@Autowired
    UserRoleRepository userRoleRepository;
	
	private UserRole createUserRole(){
        UserRole userRole = userRoleRepository.findByName("ROLE_ADMIN");
        if(userRole == null){
            userRole = new UserRole();
            userRole.setName("ROLE_ADMIN");
            userRole = userRoleRepository.save(userRole);
        }
        return userRole;
    }

    private User createUser(){
        UserRole userRole = createUserRole();

        User user = new User();
        user.setUsername("batman");
        user.setPassword("bat");
        user.setUserRole(userRole);

        return user;
    }    
}
```

Now let's test creating a user in the database.

```
import org.junit.Test;

import com.example.springbootmonolith.repository.UserRepository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class UserIntegrationTest {
...
	@Autowired
    UserRepository userRepository;
    
	@Test
    public void signup_User_Success() {
    	User user = userRepository.findByUsername("batman");
        if(user != null) {
            userRepository.delete(user);
        }
        user = createUser();
        user = userRepository.save(user);
        User foundUser = userRepository.findByUsername(user.getUsername());

        assertNotNull(user);
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());

        userRepository.delete(user);
    }
...
}
```

In integration test you can also test constraints put on columns in the database. Like in the nest test we will check for exception if username is duplicate.

```
import org.springframework.dao.DataIntegrityViolationException;

public class UserIntegrationTest {
...
	@Test(expected = DataIntegrityViolationException.class)
    public void signup_DuplicateUsername_Exception() {
        User user = createUser();
        userRepository.save(user);
        user.setId(null);
        userRepository.save(user);
    }
...
}
```

### You Do
Write a test case for delete user by user Id `deleteById()`.

```
import static org.junit.Assert.assertNull;

public class UserIntegrationTest {
...
	@Test
    public void deleteById_User_Success(){
        User user = userRepository.findByUsername("batman");
        if(user==null) {
            createUser();
            user = userRepository.save(user);
        }
        userRepository.deleteById(user.getId());
        User foundUser = userRepository.findById(user.getId()).orElse(null);

        assertNull(foundUser);
    }
...
}
```

Let's also try to test `addCourse()`,

```
public class UserIntegrationTest {
...
	@Test
    public void addCourse_User_Success() {
        User user = userRepository.findByUsername("batman");
        if(user == null) {
            user = createUser();
            user = userRepository.save(user);
        }
        Course course = courseRepository.save(createCourse());

        user.addCourse(course);

        userRepository.save(user);

        assertNotNull(user);
        assertNotNull(user.getCourses());
        assertEquals(user.getCourses().get(0).getCode(), course.getCode());
    }
...
}
``` 

**UserIntegrationTest**

```
package com.example.springbootmonolith.integration;

import com.example.springbootmonolith.model.Course;
import com.example.springbootmonolith.model.User;
import com.example.springbootmonolith.model.UserRole;
import com.example.springbootmonolith.repository.CourseRepository;
import com.example.springbootmonolith.repository.UserRepository;
import com.example.springbootmonolith.repository.UserRoleRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("qa")
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserIntegrationTest {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    private UserRole createUserRole(){
        UserRole userRole = userRoleRepository.findByName("ROLE_ADMIN");
        if(userRole == null){
            userRole = new UserRole();
            userRole.setName("ROLE_ADMIN");
            userRole = userRoleRepository.save(userRole);
        }
        return userRole;
    }

    private User createUser() {
        UserRole userRole = createUserRole();

        User user = new User();
        user.setUsername("batman");
        user.setPassword("bat");
        user.setUserRole(userRole);

        return user;
    }

    private Course createCourse(){
        Course course = new Course();
        course.setCode("SHI");
        course.setName("SuperHero Immersive");

        return course;
    }

    @After
    public void removeUser(){
        User foundUser = userRepository.findByUsername("batman");
        if(foundUser!=null)
            userRepository.delete(foundUser);
    }

    @Test
    public void signup_User_Success(){
        User user = createUser();
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());

        User foundUser  = userRepository.findByUsername(savedUser.getUsername());

        assertNotNull(foundUser);
        assertEquals(savedUser.getId(), foundUser.getId());

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void signup_DuplicateUsername_Exception(){
        User user = createUser();
        user = userRepository.save(user);
        user.setId(null);
        userRepository.save(user);
    }

    @Test
    public void deleteById_User_Success(){
        User user = createUser();
        userRepository.save(user);

        assertNotNull(user.getId());

        userRepository.deleteById(user.getId());
        User deletedUser = userRepository.findByUsername("batman");

        assertNull(deletedUser);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void deleteById_UserNotExists_Exception(){
        userRepository.deleteById(1L);
    }

    @Test
    public void addCourse_UserAndCourse_Success() {
        User user = createUser();
        userRepository.save(user);

        Course course = createCourse();
        courseRepository.save(course);

        assertNotNull(course.getId());

        course = courseRepository.findById(course.getId()).orElse(null);

        assertNotNull(course);

        user.addCourse(course);
        userRepository.save(user);

        assertNotNull(user);
        assertNotNull(user.getCourses());
        assertNotNull(user.getCourses().get(0));
        assertEquals(user.getCourses().get(0).getId(), course.getId());
    }
}

```

## Feature Testing

**SignupTest**

```
package com.example.springbootmonolith.feature;

import com.example.springbootmonolith.model.User;
import com.example.springbootmonolith.model.UserRole;
import com.example.springbootmonolith.repository.UserRepository;
import com.example.springbootmonolith.repository.UserRoleRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("qa")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignupTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void a_signup_User_Success() throws Exception {

        UserRole userRole = new UserRole();
        userRole.setName("ROLE_ADMIN");
        userRoleRepository.save(userRole);

        User user = new User();
        user.setUsername("batman");
        user.setPassword("bat");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserInJson(user.getUsername(), user.getPassword(), userRole.getName()));

        MvcResult token = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(token);
        System.out.println(token.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(username = "batman", password = "bat", roles = {"ADMIN"})
    public void b_delete_User_Success() throws Exception {
        User user = userRepository.findByUsername("batman");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user/"+user.getId());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    private static String createUserInJson (String name, String password, String roleName) {
        return "{ \"username\": \"" + name + "\", " +
                "\"password\":\"" + password + "\", " +
                "\"userRole\": { \"name\": \"" + roleName +"\" }}";
    }
}

```
