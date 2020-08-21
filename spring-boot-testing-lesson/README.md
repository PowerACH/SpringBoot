
| Title | Type | Duration | Author |
| -- | -- | -- | -- |
| Spring Boot Testing| Lesson | 3:00 | Isha Arora |

# ![](https://ga-dash.s3.amazonaws.com/production/assets/logo-9f88ae6c9c3871690e33280fcf557f33.png) Unit Testing With Mocks and Stubs

### Learning Objectives

*After this lesson, students will be able to:*

- Differentiate between mocking and stubbing and explain when you'd use each.
- Mock dependencies in order to test a Spring Boot application.
- Use stubbing to test a Spring Boot application.

### Lesson Guide

| TIMING  | TYPE  | TOPIC  |
|:---:|---|--- |
| 5 min  | Opening  | Lesson Objectives |
| 5 min  | Introduction  | Mocks vs. Stubs |
| 30 min | Guided Practice  | Testing the Controller |
| 10 min | Independent Practice  | Testing the Signup API |
| 30 min | Guided Practice  | Testing the Service Layer |
| 10 min | Independent Practice  | Testing the `createUser()` Method |
| 5 min  | Introduction  | Stubbing |
| 30 min | Guided Practice  | Testing the Controller |
| 10 min | Independent Practice  | Stubbing Practice |
| 30 min | Guided Practice  | Testing the Service Layer |
| 10 min | Independent Practice  | Stubbing Practice |
| 5 min  | Conclusion  | Review/Recap |

## Opening (5 min)

We have previously discussed testing and what that means. We have also talked about unit testing and how to test Java code using JUnit. We're now going to learn about unit testing in Spring Boot. 

### Mocks vs. Stubs 

Spring Boot comes with its own set of Maven dependencies for testing. It should automatically be a part of your `pom.xml`. Make sure to verify that the dependency exists in your project before moving forward:

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-test</artifactId>
	<scope>test</scope>
</dependency>
```

You already know about JUnit tests; what we're going to learn now is **mocking**.

-----

## Mocking (5 min)

Mocking is used in unit testing to simulate the dependencies on which the object you want to test is dependent. By mocking, you model the behavior of a real object upon which the object you are testing is dependent. 

![](https://cdn-images-1.medium.com/max/1200/1*fCMBDvJQWR6KokIF-H7iwQ.png)

For instance, in your previous Spring application, you had a `UserController` that was calling the `UserService` object to process the request and get the response. What if you want to test the `createUser()` method in the `UserController` without really bothering with the service method that's called in the method? 

In such cases, mocking is the answer. You will mock a `userService.createUser()` call to simulate a dummy user object so that, when your test runs, instead of getting null, it actually receives the previously created dummy object. The example above will be much more clear once we start writing our tests by mocking the objects.

[Mockito](https://site.mockito.org) is the most popular mocking framework for Java.

-----

## Code-Along: Testing the Controller (30 min)

Copy the solution code from an application we built previously (in [spring-boot-security-lesson](../spring-boot-security-lesson), [spring-data-postgresql-lesson](../spring-data-postgresql-lesson) or [spring-data-mongodb-lesson](../spring-data-mongodb-lesson)). We'll write unit tests for `UserController`. 

By convention, all test classes are placed under the `src/test/java` folder. Create a package under this path with the name `com.example.springbootmonolith.controller`. Under this package, create a new class: `UserControllerTest`. This is a widely accepted convention for the test classes. You keep the class name the same as the class you are testing and add `'Test'` as the suffix:

```
package com.example.springbootmonolith.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    /**Main entry point for server-side Spring MVC test support.**/
    @Autowired
    private MockMvc mockMvc;
}
```

**`@RunWith(SpringRunner.class)`** annotation tells JUnit to run using Spring's testing support. SpringRunner provides support for loading Spring *ApplicationContext*, autowiring beans into the test class.

**`@WebMvcTest`** is used for auto-configuring Spring MVC and Jackson converters. Additionally, it loads relevant components such as `@Controller`, `@RestController`, etc. and configures `MockMvc`.

Now let's add our first test. But before that, if you removed or commented out the `hello()` method we wrote earlier in our `UserController.java`, go back and uncomment/add that. This was the method we wrote previously:

```
@RestController
public class UserController {
...
	@GetMapping("/hello")
	public String helloWorld(){
	    return "Hello World!!";
	}
...
}
```

Now, go back to `UserControllerTest.java` and add a test method for it. The developer community is very picky about the method names people give to test methods. There are a few different accepted ways of doing that. For now, we'll keep things simple and use one of those ways to give our test method a name. The convention is `methodName_StateUnderTest_ExpectedBehavior`. The case of the method name also varies by different users. You can read more about different naming conventions [here](https://dzone.com/articles/7-popular-unit-test-naming).

```
import org.junit.Test;

@Test
public void helloWorld_ReturnsString_Success() throws Exception {
}
```

The first thing we need to do is mock the `GET` request. In our unit tests for controller, we have to mock the HTTP requests. For this, we'll create a `RequestBuilder` object. We'll use `MockMvcRequestBuilders` to send a request as `/hello`. Make sure you use the exact same endpoint as is given in the controller:

```
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

RequestBuilder requestBuilder = MockMvcRequestBuilders
            .get("/hello")
            .accept(MediaType.APPLICATION_JSON);
```                

Next, we'll use the `mockMvc` object we injected initially and call `perform()` on it:

```
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().string("Hello World!!"));
```

> `perform()` will call a `get()` request method, which returns the `ResultActions`. Using this result, we can have assertion expectations on response, such as content, HTTP status, header, and so on.

> `andExpect()` will expect the provided argument. In our case, we are expecting “index” to be returned via `MockMvcResultMatchers.view()`.

Finally, your test method will look like:

```
@Test
public void helloWorld_ReturnsString_Success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/hello")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!!"));
}
```

> Test methods always return "void," as they are not built to return any value.

Right click and run as test. You'll see multiple errors, but don't let them intimidate you. Scroll up to the line where the error originates, and you'll see something like:

`java.lang.IllegalStateException: Failed to load ApplicationContext`

This error mostly occurs because your Spring container has failed to initialize a bean on its own. In our case, it's because our `UserController` has `UserService` injected into it, so we'll also have to mock the injected bean before running our test.

We have to inject the `UserService` bean into our class. We can use the **@MockBean** to add mock objects to the Spring application context. The mock will replace any existing bean of the same type in the application context. If no bean of the same type is defined, a new one will be added. This annotation is useful in integration tests in which a particular bean (for example, an external service) needs to be mocked.

According to [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-mvc-tests), @WebMvcTest will often be limited to a single controller and used in combination with @MockBean to provide mock implementations for required collaborators.

To use this annotation, we have to use SpringRunner to run the test:

```
@MockBean
private UserService userService;
```

After we mock the dependencies, we're going to run our test class again and evaluate the result. You're still getting the same error but this time, if you scroll through the error log, you'll see something like:

`Field jwtUtil in com.example.springbootmonolith.config.JwtRequestFilter required a bean of type 'com.example.springbootmonolith.config.JwtUtil' that could not be found.`

We're getting this error because we are mocking the HTTP request on which the filter will be called by our security component; `JwtRequestFilter` class injects `JwtUtil` object. So, we'll also have to mock this bean in our `UserControllerTest`:

```
@MockBean
private JwtUtil jwtUtil;
```

Now we'll run our test class again. If there are no errors and the test succeeded, you should see a green line on the left of the console, which means the test has completed successfully.

Congratulations! You have successfully tested your first-ever controller method.

Next, we'll write another test to mock one of our actual request methods. Let's start with `login()`. In our `login()` method, we call a method of `UserService` that returns a JWT (of the String data type). We'll mock this very call in our test case. First off, let's give a nice detailed name to our method following the convention we discussed previously:

```
@Test
public void login_Returns200_Success() throws Exception {
}
```

Like before, we'll create a `RequestBuilder` that can make an HTTP request to a `/login` API. In this case, however, our API accepts a JSON object that it converts to our `User` object:

```
RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(createUserInJson("batman","robin"));
```

We'll create a util method in our test class to convert username and password to a JSON format string, which will then be converted to a `User` object by our controller. Add the following method to `UserControllerTest` class:

```
public class UserControllerTest {
...
	private static String createUserInJson (String name, String password) {
	    return "{ \"name\": \"" + name + "\", " +
	            "\"password\":\"" + password + "\"}";
	}
...
}
```

Let's return to our test method and mock the call to `login()` method in `UserService` class. We do this using a couple of static methods provided by Mockito. The methods are pretty intuitive, and there's a lot you can do with them:

```
when(userService.login(any())).thenReturn("123456");
```

What we are saying here is that, as the test is running, when the control reaches `userService.login()`, a String is returned: `123456`. Again, `any()` in this case is a pre-built method that's a broad term for whatever the `login()` method is accepting as input. What you just did is a classic example of mocking a method call without actually calling it. 

Do you see how easy this makes our lives as developers? We are only testing our controller method and whatever external dependencies or resources our method needs will be mocked.

Finally, we'll simply call our `requestBuilder` and verify the response we get:

```
mockMvc.perform(requestBuilder)
	    .andExpect(status().isOk());
```

There's one small thing you can also do; this will prove especially useful when you write integration tests. We can store the response we get after executing the `requestBuilder`. Update the code you just wrote with this one change:

```
MvcResult result = mockMvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().json("{\"token\":\"123456\"}"))
        .andReturn();

System.out.println(result.getResponse().getContentAsString()); 
```

> The `andReturn()` method returns the response after the method executes. We can store it in the `MvcResult` object, which we will then use (for now) to output the string.

This is how the `login_Returns200_Success()` method should look:

```
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {
...
	@Test
    public void login_Success() throws Exception{

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserInJson("joe","abc"));

        when(userService.login(any())).thenReturn("123456");

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"123456\"}"))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }
...
}
```

This is what the complete `UserControllerTest` class will look like:

**UserControllerTest**

```
package com.example.springbootmonolith.controller;

import com.example.springbootmonolith.config.JwtUtil;
import com.example.springbootmonolith.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    /**Main entry point for server-side Spring MVC test support.**/
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void helloWorld_ReturnsString_Success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/hello")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!!"));
    }

    @Test
    public void login_Success() throws Exception{

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserInJson("joe","abc"));

        when(userService.login(any())).thenReturn("123456");

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"123456\"}"))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    private static String createUserInJson (String name, String password) {
        return "{ \"name\": \"" + name + "\", " +
                "\"password\":\"" + password + "\"}";
    }
}
```

------

## You Do (10 min)

Take 10 minutes to quickly write a unit test for the `/signup` API. It will be done in exactly the same way as we tested `/login`.

-----

## Testing the Service Layer (30 min)

We'll now unit test our `UserService` class. First, create a new package, `com.example.springbootmonolith.service`, under the `src/test/java` folder. Then, create a new class, `UserServiceTest`, under the newly created package:

```
package com.example.springbootmonolith.service;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
}
```

We're still using the Mockito framework in Spring, except for the `MockitoJUnitRunner` class. Remember, `SpringRunner.class` is only used for MVC layers (or what we call controller methods). To enable Mockito annotations and some other useful features, we'll run our tests using `MockitoJUnitRunner.class`. 

With `MockitoJUnitRunner.class`, we'll use **@InjectMocks** to inject a bean into our class. We can only use this annotation with classes that can be instantiated; basically, a class that you can call on a `new` operator to initialize. 

We'll use **@Mock** annotation to mock a class or interface. It minimizes the repetitive mock creation code. Keep in mind that `Mockito.mock()` is another representation of `@Mock`.

We'll now mock all of our dependencies that `UserService` class has injected:

```
import com.example.springbootmonolith.models.User;
import com.example.springbootmonolith.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

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
    private UserServiceImpl userService;


    @InjectMocks
    private User user;

}
```

> We've used `@InjectMocks` to mock the `UserServiceImpl` and `User` classes because we'll use their instances to call other methods or use them as objects. For others, we really don't care if they are initialized while being mocked. This will become clear in the next few steps.

We'll test our `getUser()` method. The definition of this method is `public User getUser(String username)`. It accepts a String and returns a `User` object. We'll first create a dummy user object and then mock the call to `userRepository`:

```
@Test
public void getUser_ReturnsUser_Success(){

    user.setUsername("batman");
    user.setPassword("robin");

    when(userRepository.findByUsername(anyString())).thenReturn(user);

    User tempUser = userService.getUser(user.getUsername());

    assertEquals(tempUser.getUsername(), user.getUsername());
}
```

In the code above, both the `user` and `userService` instances are used to initialize and call another method; that's why they were injected using `@InjectMocks`. On the other hand, we're just mocking the `userRepository.findByUsername()` call; that's why we use `@Mock`.

Let's also test our `login()` method, but with a twist. The unit tests are not created to only test happy paths; their prime objective is to test every inch of the code. This time, in `login()`, we'll test the "else" portion of "If the user is not found..."

```
@Test
public void login_UserNotFound_Error(){
    user.setUsername("batman");
    user.setPassword("robin");

    when(userRepository.findByUsername(anyString())).thenReturn(null);

    String token = userService.login(user);

    assertEquals(token, null);
}
```

Did you notice we have code for `user` initialization that's being repeated? When we know there will be dummy objects that will be reused, we put them all under **@Before** annotation in one method. The method marked by this annotation will be the first to run before the tests start executing:

```
@Before
public void initializeDummyUser(){
    user.setUsername("batman");
    user.setPassword("robin");
}
```

This is what the complete `UserServiceTest` class will look like:

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.config.JwtUtil;
import com.example.springbootmonolith.models.User;
import com.example.springbootmonolith.repositories.CourseRepository;
import com.example.springbootmonolith.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
```

Run all of the tests to make sure everything is running smoothly.

-----

## You Do (10 min)

Pair up! You have 10 minutes to quickly write two unit tests for the `createUser()` method: One happy path if the user is found; another if the user is not found.

-----

## Stubbing (5 min)

The **stubbing** approach is easy to use and involves no extra dependencies for the unit test. The basic technique is to implement the collaborators as concrete classes that only exhibit a small part of the overall behavior of the collaborator that's needed by the class undergoing testing. Stubbing is another way of simulating the behavior of an object's method. 

![](https://cdn-images-1.medium.com/max/1600/0*KdpZaEVy6GNnrUpB.png)

------

## Code-Along: Testing the Controller (30 min)

In this code-along, we'll focus on testing `UserProfile`. Just like before, we'll start with the controller layer and go until the service layer.

Before we actually start writing tests, let's first make some changes to the `UserServiceImpl` class to facilitate later changes. Right now, we're injecting dependencies into a bean directly using fields through reflection. Check out this example:

```
@Autowired
UserProfileService userProfileService;
```

One of the major disadvantages is that classes cannot be instantiated without reflection (you'll see an example of this shortly); you need the Spring container to instantiate them. When writing unit tests using stubs, we need a certain level of control over the dependencies injected in a bean, which field injection unfortunately prevents us from having. 

To remedy this, we'll change our dependency injection (DI) method from field injection to setter injection in the `UserProfileController` class. We'll only update the `userProfileService ` dependency by creating a setter method for it. Everything else will remain as it is in the class:

```
@RestController
@RequestMapping("/profile")
public class UserProfileController {
...
	private UserProfileService userProfileService;

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }
...
}
```

Create a new test class under the controller package `UserProfileControllerTest`. We don't need any additional classes to write a test using stubs. For now, we'll only create the class and add the instance of the class we're testing:

```
package com.example.springbootmonolith.controller;

public class UserProfileControllerTest {

    private UserProfileController userProfileController;
}
```

If we return to `UserProfileController`, we just created a setter method that accepts a type of `UserProfileService` interface to instantiate the `userProfileService` object. We'll create a dummy implementation of the `UserServiceImpl` class that actually implements the `UserProfileService` interface. Create a new class under `service` package in the `/test` folder `UserProfileServiceStub`:

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.model.UserProfile;

public class UserProfileServiceStub implements UserProfileService {

    @Override
    public UserProfile createUserProfile(String username, UserProfile newProfile) {
        return null;
    }

    @Override
    public UserProfile getUserProfile(String username) {
        return null;
    }
}

```

We'll leave this class as-is for now and go back to `UserProfileControllerTest` to initialize the `userProfileController` object now that we have a dummy class implementation of `UserProfileService`. We'll use the `@Before` annotation for initialization:

```
import com.example.springbootmonolith.service.UserProfileServiceStub;
import org.junit.Before;
import org.junit.Test;

public class UserProfileControllerTest {

    private UserProfileController userProfileController;

    @Before
    public void initializeUserProfileController(){
        userProfileController = new UserProfileController();
        userProfileController.setUserProfileService(new UserProfileServiceStub());
    }
}
```

Finally, we'll start writing our test method for `createUserProfile()`; this is a `POST` request to create a user profile:

```
public class UserProfileControllerTest {
...
	@Test
	public void createUserProfile_SaveUserProfile_Success(){
		UserProfile userProfile = new UserProfile();
        userProfile.setEmail("batman@superhero.com");

        UserProfile newProfile = userProfileController.createUserProfile("batman", userProfile);

        Assert.assertNotNull(newProfile);
        Assert.assertEquals(newProfile.getEmail(), userProfile.getEmail());
	}
...
}
```

> We can also send a HTTP request using `RequestBuilder`, just as we did in mocking. 

There's just one step left. Our implementation of the `createUserProfile()` method in `UserProfileServiceStub` is still returning null. We need to update its implementation to return a dummy user profile:

```
public class UserProfileServiceStub implements UserProfileService {
...
    @Override
    public UserProfile createUserProfile(String username, UserProfile newProfile) {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("batman@superhero.com");

        return userProfile;
    }   
...
}
```

Run your `UserProfileControllerTest` class. The test should pass.

-----

## You Do (10 min)

Pair up! Take 10 minutes to write the test case for `getUserProfile()` in `UserProfileController`, just as we did before.

-------

## Testing the Service Layer (30 min)

Time to test our `UserProfileServiceImpl` class. This will be a little more complicated than the controller. With regard to service, we also have to create a stub for the data access layer. 

As with earlier, the first thing we'll do is update `UserProfileServiceImpl` to change field injection to constructor injection. Again, we're only updating the autowiring of beans:

```
public class UserProfileServiceImpl implements UserProfileService {
...
    private UserProfileRepository userProfileRepository;

    private UserService userService;

    @Autowired
    public UserProfileServiceImpl(UserService userService, UserProfileRepository userProfileRepository){
        this.userService = userService;
        this.userProfileRepository = userProfileRepository;
    }
...
}
```

Create the test class, `UserProfileServiceTest`, under the `service` package, declaring the `userProfileService` instance:

```
package com.example.springbootmonolith.service;

public class UserProfileServiceTest {

    private UserProfileServiceImpl userProfileService;
}
```

Your `UserProfileServiceImpl` has two dependencies in it: `UserProfileRepository` and `UserService`. As before, we'll be creating stub classes of both. Let's start with the `UserServiceStub` class under the `service` package; it will implement the `UserService` interface and all of its methods:

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceStub implements UserService {

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public Iterable<User> listUsers() {
        return null;
    }

    @Override
    public String createUser(User newUser) {
        return null;
    }

    @Override
    public String login(User user) {
        return null;
    }

    @Override
    public HttpStatus deleteById(Long userId) {
        return null;
    }

    @Override
    public User addCourse(String username, int courseId) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
```

After this, we'll create a stub implementation of `UserProfileRepository`. This is a little tricky, and it may seem like a hacky way of doing things, but there's no decent or universally accepted way of stubbing a data access layer. Usually companies create a dummy database just for testing. Because we're in a learning phase, we'll stick with stub implementation, but it may seem like the more difficult option.

Create a new package, `repository`, in the `/test` folder. Create a new class, `UserProfileRepositoryStub`, under this package. Implement the `UserProfileRepository` interface, and don't be afraid to override all of the methods your IDE is suggesting. You'll see lots of compilation errors, but there is a simple fix: Replace `T` with `UserProfile` and all of the errors will go away. Finally, your class will look something like this:

```
package com.example.springbootmonolith.repository;

import com.example.springbootmonolith.model.UserProfile;

import java.util.Optional;

public class UserProfileRepositoryStub implements UserProfileRepository {

    @Override
    public <S extends UserProfile> S save(S entity) {
        return null;
    }

    @Override
    public <S extends UserProfile> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<UserProfile> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<UserProfile> findAll() {
        return null;
    }

    @Override
    public Iterable<UserProfile> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(UserProfile entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends UserProfile> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public UserProfile findProfileByUsername(String username) {
        return null;
    }
}
```

Now that we've created stub implementation for both of our dependencies, we'll go back to the `UserProfileServiceTest` class and complete initialization of the `userProfileService` object:

```
import com.example.springbootmonolith.model.UserProfile;
import com.example.springbootmonolith.repository.UserProfileRepositoryStub;

import org.junit.Before;

public class UserProfileServiceTest {
...
    private UserProfileServiceImpl userProfileService;

    @Before
    public void initializeUserProfile(){
        userProfileService = new UserProfileServiceImpl(new UserServiceStub(), new UserProfileRepositoryStub());
    }
...
}
```

We'll write our test method for `createUserProfile()`:

```
import org.junit.Assert;
import org.junit.Test;

public class UserProfileServiceTest {
...
	@Test
    public void createUserProfile_AddsProfile_Success(){

        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("batman@superhero.com");

        UserProfile newProfile = userProfileService.createUserProfile("batman", userProfile);

        Assert.assertNotNull(newProfile);
        Assert.assertEquals(newProfile.getEmail(), userProfile.getEmail());
    }
...
}
```

We'll update the implementation of methods in both of the stub classes we created so that they return the dummy user profile we want them to return:

```
public class UserServiceStub implements UserService {
...
    @Override
    public User getUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
...
}
```

```
public class UserProfileRepositoryStub implements UserProfileRepository {
...
    @Override
    public <S extends UserProfile> S save(S entity) {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("batman@superhero.com");

        return (S)userProfile;
    }
...
}
```

Run the `UserProfileServiceTest` class to make sure the test passes.

-----

## You Do (10 min)

Pair up! Take 10 minutes to write the test case for `getUserProfile()` in `UserProfileServiceImpl`, just as we did before.

-----

## Conclusion (5 min)

There are many different ways of using both mocks and stubs. They're often intertwined together to test the entire app. If you feel the way this guy is feeling, welcome to dev world, where confusion is a constant state of mind!

![](https://cdn-images-1.medium.com/max/1200/1*BgRyr8a2hrKSQO4QzXzs1w.jpeg)

### Lab: Spring Music App

Finally, we have come to testing our music app. Follow the instructions provided in [Step 5](https://git.generalassemb.ly/GA-Cognizant/spring-boot/tree/master/spring-boot-lab#step-5-spring-testing) of the [lab](https://git.generalassemb.ly/GA-Cognizant/spring-boot/tree/master/spring-boot-lab). 

### Additional Resources

- [Test Doubles — Fakes, Mocks and Stubs](https://blog.pragmatists.com/test-doubles-fakes-mocks-and-stubs-1a7491dfa3da)

