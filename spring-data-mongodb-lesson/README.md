| Title | Type | Duration | Author |
| -- | -- | -- | -- |
| Spring Data with Mongo | Lesson | 2:00 | IA |

# Spring Data

### LEARNING OBJECTIVES

*After this lesson, you will be able to:*
- Use Spring Data to bring data from MongoDB into your Spring Boot application.
- Choose between embedded and referenced data in your application.

### LESSON GUIDE

__TODO__

| TIMING  | TYPE  | TOPIC  |
|:---:|---|--- |
| 10 min | Opening  | Discuss lesson objectives |
| 15 min | Guided Practice  | Spring Data and MongoDB |
| 20 min | Independent Practice  | Spring Data and MongoDB |
| 15 min | Guided Practice  | Embedded Data  |
| 15 min | Guided Practice  | Referenced Data  |
| 15 min | Guided Practice  | @DBRef  |
| 20 min | Discussion  | Embed or Reference? |
| 5 min  | Conclusion  | Review / Recap |

## Opening (10 mins)

[Spring Data’s](https://spring.io/projects/spring-data) mission is to provide a familiar and consistent, Spring-based programming model for data access while still retaining the special traits of the underlying data store.

It makes it easy to use data access technologies, relational and non-relational databases, map-reduce frameworks, and cloud-based data services.

You already know what MongoDB is, now it's time to learn how Spring Boot uses Spring Data to talk to MongoDB.

The [Spring Data](https://spring.io/projects/spring-data-mongodb) MongoDB project provides integration with the MongoDB document database. Key functional areas of Spring Data MongoDB are a POJO centric model for interacting with a MongoDB DBCollection and easily writing a Repository style data access layer.

Some of its very useful features are,

- Spring configuration support using Java based @Configuration classes or an XML namespace for a Mongo driver instance and replica sets.
- MongoTemplate helper class that increases productivity performing common Mongo operations. Includes integrated object mapping between documents and POJOs.
- Feature Rich Object Mapping integrated with Spring’s Conversion Service
- Annotation based mapping metadata but extensible to support other metadata formats
- Java based Query, Criteria, and Update DSLs
- Automatic implementation of Repository interfaces including support for custom finder methods.
- QueryDSL integration to support type-safe queries.
- Cross-store persistance - support for JPA Entities with fields transparently persisted/retrieved using MongoDB

-----

## Code Along (15 min)

This lesson will be a continuation of the application we built in [Spring Profiles](../spring-profiles-lesson/README.md) lesson. Download the starter-code given [here](./spring-data-mongodb-lesson/starter-code) and build on this app for the rest of the lesson. 

So far we only have a `UserController` class with one `helloWorld()` method which will return us a string. Let's run this app and make sure the endpoint [http://localhost:8080/hello](http://localhost:8080/hello) is working.

Also we have two properties file `application.properties` and `application-dev.properties` in the app. This is done to create different profiles for different environments. We will come back to these in few minutes.

### Add Maven Dependency

So first thing we need to do is add Maven dependency for MongoDB in `pom.xml`.

**pom.xml**

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### Update DB properties

Go to `application-dev.properties` under `resources/` folder and add the following properties to it.

```
spring.data.mongodb.database=user_db
spring.data.mongodb.port=27017
spring.data.mongodb.host=localhost
#spring.data.mongodb.authentication-database=admin
#spring.data.mongodb.username=root
#spring.data.mongodb.password=root
```

> Whether you have to give the username and password to connect to the database depends on how you configured MongoDB while installing it.

### Create User model

Let's create a new class `User` under `model` package which will be persisted as a collection in our database.

**User**

```
package com.example.springbootmongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document
public class User {

    @Id
    private String id;

	@Indexed(unique = true)
    private String username;
   
    private String password;

    public User(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

**@Document** is equivalent to @Entity for SQL databases. It is used to define a collection in MongoDB. Also, remember in MongoDB all Ids are uniquely generated UUIDs by the database itself. That's why we only use @Id annotation and nothing else.

**@Indexed** annotation tells the mapping framework to call ensureIndex on that property of your document, making searches faster. In the above case we are indexing `username` to make search user by username faster.

### Add Data layer

We are going to start from bottom and add the data layer first and then the rest. Go ahead create a `UserRepository` interface under `repository` package. We will extend `MongoRepository` interface in it. This is Spring Data's implementation. Behind the scenes this will run normal queries for data manipulation and retrieval.

**UserRepository**

```
package com.example.springbootmongo.repository;

import com.example.springbootmongo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
}
```

`MongoRepository` interface is similar to `CrudRepository` that we used in Postgres lesson for SQL database. `MongoRepository` is also provided by Spring Data with some already created methods to access the database.

### Add Service layer

Now we will first create `UserService` interface under `service` package. For now we will only create one method to retrieve all documents from user collection.

**UserService**

```
package com.example.springbootmongo.service;

import com.example.springbootmongo.model.User;

import java.util.List;

public interface UserService {

    public List<User> listUsers();

}
```

Then, we will add a `UserServiceImpl` class implementing this interface.

**UserServiceImpl**

```
package com.example.springbootmongo.service;

import com.example.springbootmongo.model.User;
import com.example.springbootmongo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
```

### Update Controller

Finally now we will add a GET method to our existing `UserController` class to fetch all the user data.

**UserController**

```
package com.example.springbootmongo.controller;

import com.example.springbootmongo.model.User;
import com.example.springbootmongo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/list")
    public List<User> listUsers(){
        return userService.listUsers();
    }
}

```

Run the server and test the new route [http://localhost:8080/list](http://localhost:8080/list). You will get an empty array back, there is no data in your database yet. The positive news is that there is no error in your app.

------

## You Do (20 min)

Let's create a route to save some user data in our database. You have to follow the steps given above in the same order and something we have previously done in Postgres lesson. Pair up for this exercise.

<details><summary>**UserService**</summary>

```
public interface UserService {
...

    public User createUser(User newUser);
    
...
}
```
</details>

<details><summary>**UserServiceImpl**</summary>

```
public class UserServiceImpl implements UserService {
...

    @Override
    public User createUser(User newUser) {
        return userRepository.save(newUser);
    }

...
}
```
</details>

<details><summary>**UserController**</summary>

```
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class UserController {
...

    @PostMapping("/signup")
    public User createUser(@RequestBody User newUser){
        return userService.createUser(newUser);
    }

...
}	
```
</details>

Restart the server. Test the POST request [http://localhost:8080/signup](http://localhost:8080/signup) with request body,

```
{
	"username":"batman",
	"password":"robin"
}
```
Respone you will get will be a JSON object.

```
{
    "id": "5d1c1515559f981065040df7",
    "username": "batman",
    "password": "robin"
}
```
> The id is a UUID which is created by MongoDB.

-----

## Embedded Data (15 min)

You already know what embedded data means in MongoDB, we will now see a way to implement that using Spring Data. Even though relational database mapping does not exist in NoSql databases but if we have to compare it with that then one-to-one and one-to-many relationship is implemented using embedded data.

For our example we will now create a User Profile for each user and that will be embedded in our user object. Go ahead under `model` package create `UserProfile` class. 

**UserProfile**

```
package com.example.springbootmongo.model;

import org.springframework.data.mongodb.core.index.Indexed;

public class UserProfile {

    @Indexed(unique = true)
    private String email;

    private String mobile;

    private String address;

    public UserProfile() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
```

> We will keep email unique. Assume it was a design decision. Also, later you will see its use when we will add a method to find a user by email.

Now we will update `User` model to add `UserProfile` in it.

```
public class User {
...

    private UserProfile userProfile;

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
    
...
}
```

Restart the server and test the changes by creating a new user with user profile info.

```
{
	"username":"superman",
	"password":"fly",
	"userProfile" : {
		"email":"superman@superhero.com"
	}
}
```

We will now add a method to find user by email. For that we will update `UserRepository` interface with the said method. The convention is 'findBy' and then the name of the field with first letter starting from caps. In this case we have to find the user by email, so that's why method name is `findByEmail`.

```
public interface UserRepository extends MongoRepository<User, String>{

    @Query("{'userProfile.email': ?0}")
    public User findByEmail(String email);
}
```

As the `UserProfile` object is embedded in `User` object we cannot just use `findBy` way of creating our custom method. We also have to give the field is under which object. In the above case we are using `@Query` annotation to do that. `?0` means replace with the first input argument of the method.

Let's make relevant changes in Service and Controller classes. 

**UserService**

```
public interface UserService {
...
	public User findUserByEmail(String email);
...
}
```

**UserServiceImpl**

```
public class UserServiceImpl implements UserService {
...
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
...
}
```

**UserController**

```
public class UserController {
...
    @GetMapping("/user/{email}")
    public User getUserByEmail(@PathVariable String email){
        return userService.findUserByEmail(email);
    }
...
}
```

Run the server, and test the GET API [http://localhost:8080/user/superman@superhero.com](http://localhost:8080/user/superman@superhero.com)

Even though embedded data is the way to go in MongoDB but there can be instances when you want to reference the data that means store just an id of the document instead of embedding the entire object. 

------

## Referenced Data (15 min)

If you remember from your lesson on MongoDB we do that for data which is subject to change and is expected to use multiple documents. In our case, what we want now is for a user to be able to enroll into courses. We will now create a Course object with only 2 fields, course `name` and `code`. Create a new `Course` class under `model` package. This will be just like `User` model with its own collection and id in the database.

**Course**

```
package com.example.springbootmongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Course {

    @Id
    private String id;

    private String code;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

```

As `Course` is a separate collection, we will create a separate repository to talk to it.

**CourseRepository**

```
package com.example.springbootmongo.repository;

import com.example.springbootmongo.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends MongoRepository<Course, String>{
}

```

Now go ahead and create Service layer for the course model with two methods `saveCourse()` to create a course and `listCourses()` to list all the courses.

**CourseService**

```
package com.example.springbootmongo.service;

import com.example.springbootmongo.model.Course;

import java.util.List;

public interface CourseService {

    public Course saveCourse(Course course);

    public List<Course> listCourses();
}

```

**CourseServiceImpl**

```
package com.example.springbootmongo.service;

import com.example.springbootmongo.model.Course;
import com.example.springbootmongo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService{

    @Autowired
    CourseRepository courseRepository;

    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public List<Course> listCourses() {
        return courseRepository.findAll();
    }
}

```

We will now create a controller for the methods we created above.

**CourseController**

```
package com.example.springbootmongo.controller;

import com.example.springbootmongo.model.Course;
import com.example.springbootmongo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseService courseService;

    @GetMapping
    public List<Course> listCourses(){
        return courseService.listCourses();
    }

    @PostMapping
    public Course saveCourse(@RequestBody Course course){
        return courseService.saveCourse(course);
    }
}
```

Run the server again and test the above routes. Let's first make sure we are able to save a new course and get a list of all courses.

POST [http://localhost:8080/course](http://localhost:8080/course)

```
{
	"code" : "SHI",
	"name" : "SuperHero Immersive"
}
```

GET [http://localhost:8080/course](http://localhost:8080/course)

----

## @DBRef (15 min)

Till now we have created our course but we don't have any functionality yet for the User to add a course. Remember multiple users can add the same course so we will just reference the `id` of the course in our User object. Spring Data will help us accomplish that using just one annotation.

DBRef annotation is used to refer to another document. Also, when the object is loaded from db, these references will be eagerly resolved and we will get back a mapped object that will look just like an embedded object stored within the document.

What we will now do is update our `User` class to have a reference of `Course` object and since a User can enroll in multiple courses at the same time, we will have a list of courses added in the `User`.

```
import org.springframework.data.mongodb.core.mapping.DBRef;

public class User {
...
	@DBRef
    private List<Course> courses;
    
    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
...
}
```

For now to test the change we will create a new user and list all users. After server restart make a POST request to create a user [http://localhost:8080/signup](http://localhost:8080/signup). The request body will be,

```
{
	"username":"spiderman",
	"password":"spider",
	"userProfile" : {
		"email":"spiderman@superhero.com"
	},
	"courses" : [
			{
				"id" : "5d1c285a559f98129115e503"
			}
		]
}
```
> Make sure you update the course id to be the id that was created earlier while you were creating a course.

You might notice that course code and name fields are null in response object after you create the course. Now make a GET request to list all users [http://localhost:8080/list](http://localhost:8080/list) and you will see course object embedded in the said user. But make no mistake the course is still referenced in the user. If you go to your terminal and access `user_db` database. Under that select all users you will see how the course is saved.

```
$ mongo
> show dbs
> use user_db
> show collections
> db.user.find()
```

You will see something like this on your terminal,

```
{ "_id" : ObjectId("5d1c2c94559f98129115e508"), "username" : "spiderman", "password" : "spider", 
"userProfile" : { "email" : "spiderman@superhero.com" }, 
"courses" : [ DBRef("course", ObjectId("5d1c285a559f98129115e503")) ], "_class" : "com.example.springbootmongo.model.User" }
```

------

## Embed or Reference? (20 min)

This is an age old question/discussion/debate in MongoDB. Whether you should reference or embed data. Let's take the app that we are building as an example. Suppose I also want each user to have a role assigned to it. That means we will now have another object `UserRole` in our app. 

#### Discuss!

What do you think should `UserRole` object be embedded or referenced in the database. Get into groups of 3-4 and discuss What would you choose and more importantly Why. Try to come to a consensus.

In objects like these this becomes more a design decision. Which is made usually on the basis of the frequency of the change. Like in our case if we foresee `UserRole` to change a lot like name of the role then it makes more sense to save it as a referenced object to maintain data consistency. But if you  see user role changing rarely then embedding the object makes sense to keep the efficiency high. 

MongoDB always makes a trade off between efficiency and consistency. 

Another rule of thumb is embed is preferred if you have one-to-one or one-to-many relationships between entities, and reference is preferred if you have many-to-many relationships.

------

## Conclusion (5 min)

Great work! You're getting to be a real database whiz - you can now use MongoDB OR Postgres in your Spring Boot applications. From here on out, the world is your oyster! (at least when it comes to using data in your apps).


