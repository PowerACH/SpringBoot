| Title | Type | Duration | Author |
| -- | -- | -- | -- |
| Spring Data and Postgres | Lesson | 3:00 | IA |

# Spring Data

### LEARNING OBJECTIVES

*After this lesson, you will be able to:*
- Explain how the Repository Pattern is used in Spring Data.
- Integrate a Spring Boot application with a Postgres database.
- Create endpoints for your application using Spring Data.
- Use annotations to get the right data from a relational database into your Spring application.

### LESSON GUIDE

| TIMING  | TYPE  | TOPIC  |
|:---:|---|--- |
| 10 min | Opening  | Discuss lesson objectives |
| 15 min | Guided Practice  | Spring Boot Database Integration |
| 15 min | Guided Practice  | Repository Pattern |
| 15 min | Guided Practice  | Spring Boot RestControllers |
| 20 min | Independent Practice  | User Signup |
| 15 min | Guided Practice  | More User Functionality |
| 30 min | Guided Practice  | Relational Mapping |
| 20 min | Independent Practice  | Creating a Course Model |
| 15 min | Guided Practice  | Join Table |
| 20 min | Independent Practice  | Testing Your APIs |
| 5 min  | Conclusion  | Review / Recap |

## Opening (10 mins)

[Spring Data’s](https://spring.io/projects/spring-data) mission is to provide a familiar and consistent Spring-based programming model for data access while still retaining the special traits of the underlying data store.

It makes it easy to use data access technologies, relational and non-relational databases, map-reduce frameworks, and cloud-based data services.

Some of its features are:
- Powerful repository and custom object-mapping abstractions
- Dynamic query derivation from repository method names
- Implementation domain base classes providing basic properties
- Possibility to integrate custom repository code
- Easy Spring integration via JavaConfig and custom XML namespaces
- Advanced integration with Spring MVC controllers

### Spring Data and JPA 

Modern web development almost always involves some kind of Object Relational Mapping (ORM) or Object Data Mapping (ODM) tool. Popular libraries such as Ruby's [ActiveRecord](http://guides.rubyonrails.org/active_record_basics.html), Node's [Mongoose](http://mongoosejs.com/), and Elixir's [Ecto](https://hexdocs.pm/ecto/Ecto.html) simplify many of the low-level database interactions that we are responsible for as software engineers. These tools provide development speed, safety, security, and ease of use that enable us to spend more time focusing on functionality and less time on complicated details.

Spring Boot provides its own unique ORM implementation that borrows from similar tools, but also augments them in many ways. Spring Data Entities and Repositories are an implementation of the [Repository Pattern](http://deviq.com/repository-pattern/) that will greatly simplify the process of connecting to and interacting with our databases in a Spring Boot environment.

-----

# Part 1

## Code Along: Spring Boot Database Integration (15 min)

This lesson is in continuation with Spring Boot overview and Spring Profile. You can either continue with the application you built previously in [Spring Profile](../spring-profiles-lesson) lesson or download the application given in the [starter-code](starter-code).

Before going further, make sure that Postgres is installed and running on your machine. To spin up our new setup and connect to the database, we'll need to install the `postgresql` dependency to our application. 

Go to `pom.xml`, paste and resolve following dependencies,

```
<dependency>
   <groupId>org.postgresql</groupId>
   <artifactId>postgresql</artifactId>
   <scope>runtime</scope>
</dependency>

<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

> The `runtime` designation simply states that this dependency is needed to run the app only. In other words, we do not need this code to `compile` our application. The `spring-boot-starter-data-jpa` is a starter for using Spring Data JPA with Hibernate.

If you have followed the previously given steps to set up database, in the file `application-dev.properties` under `resources` folder, paste the following lines.

```
spring.datasource.initialization-mode=always
spring.datasource.platform=postgres
spring.datasource.url=jdbc:postgresql://localhost:5432/myga
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create
```

> While running the server, you may get an error that says "Caused by: java.sql.SQLFeatureNotSupportedException: Method org.postgresql.jdbc.PgConnection.createClob() is not yet implemented.". Add `spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true` to `application-dev.properties` to resolve this.

Let's talk about each property one by one.

- *spring.datasource.initialization-mode* is used to load a database that is not embedded, specifically in Spring Boot 2.
- *spring.datasource.platform* is used to give the vendor name of the database the app is connecting to. This allows you to switch to database-specific scripts if necessary.
- *spring.datasource.url* is used to give the link to where database is running. In our case Postgres is running on localhost at port 5432. The database name is `myGA`.
- *spring.jpa.show-sql* is purely used for debug purpose. When set to true, you can see on console all the sql queries run.
- *spring.jpa.hibernate.ddl-auto* is used to initialize the database. It can be set to multiple values: `none`, `validate`, `update`, `create`, `create-drop`. To avoid conflicts, we turn off automatic schema creation with `spring.jpa.hibernate.ddl-auto=none`.

Now there is just one last thing left before we run our app. We have to create the database. Spring Boot will do many things for us developers but not that. Run the following command:

```
$ psql postgres
postgres=# CREATE DATABASE myga;
```

Run `\l` to confirm. Now you are all set to run your app and let the magic begin.

-----

## Repository Pattern (15 min)

The Repository Pattern is a popular way of constructing our Java classes to hide our low-level database interactions. Repositories offer an "expressive" interface for querying data. Spring Data Repositories in particular will let us query data using common methods such as `findAll()` and `findOne()`, rather than writing individual SQL queries for each database interaction.

Let's create a new `UserRepository` to see how it works. 

First, we'll need to create a class to `model` our `User`. We'll put this `User.java` class in a package called `models` right next to our `DemoApplication.java`.

Let's create our first Entity within `User.java`:

```java
package com.example.springbootmonolith.models;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

   @Id
   @Column
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(unique = true)
   private String username;

   @Column
   private String password;

   public User() {}

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
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

> "User" is a reserved keyword in Postgres. If we named our table that, we would have to write backticks or `\` every time. To avoid that, we changed the table name to `users`.

The first thing we should notice is that this looks exactly like a standard Java class, with a Constructor, Getters, and Setters. 

The constructor, however, is a "no-args" constructor. The **@Entity** annotation at the top only needs this constructor to function, so we do not need to define a default Constructor as we normally would. The rest of the annotations will tell Spring's underlying JPA code how to wire up our class to a database. 

The **@Table** annotation dictates which database table this model represents, and the **@Id**, **@GeneratedValue**, and **@Column** annotations help us manage column names and even auto-generate IDs.

Now that we have created our Entity, we'll need a `repositories` package right next to our `DemoApplication.java`. It's very important that this package lives alongside our `Application` class so that our `Repositories` will be bootstrapped with the rest of the application.

In this package, we'll create a new `UserRepository.java` file inside of this `repositories` package.

This file will allow us to perform CRUD functionality with our `USERS` table in the database, leveraging the `User` Entity we've created above. Let's drop in some starter code into `UserRepository.java` and take a look:

```java
package com.example.springbootmonolith.repositories;

import com.example.springbootmonolith.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
```

> Long is the datatype of `id` in the User object.

That's it! This code is all we need to start querying `Users` from our database. Using some Java composition magic, all we have to do is `extend` the provided Spring Boot `CrudRepository` interface, and just like that Spring will provide working classes containing several pre-built query methods. We'll explore several of these methods today when we wire this `Repository` up to a controller.

----

## Spring Boot RestControllers (15 min)

Before we start accessing data from our repository, we need to add one more layer between Controller and Repository: the Service Layer.

### Adding the Service Layer

This is the place where the business logic goes in. Remember, Spring's motto is "separation of concerns". For that to happen, we need another layer to separate business logic from controller. 

Go ahead create an interface `UserService` under `com.example.springbootmonolith.service` package. The first thing we will do is to create a User. In the interface, we will declare `listUsers()` that accepts `User` object as input. Here's what it should look like:

```java
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.models.User;

public interface UserService {

   public Iterable<User> listUsers();
}
```

### Accessing Data from the User Repository

If we take another look at our `UserRepository`, it is a Java `interface`, not a `class`. Why is that the case here?

While a class can be instantiated and methods can be called on that `instance`, an interface cannot be used directly. We will need an `instance` of `UserRepository` inside of our `UserService` if we want to use all of its methods to query the database.

Fortunately, there is a simple solution to this problem. While we might be tempted to `implement` the interface itself, we can accomplish the same thing with far fewer lines of code. 

Spring Boot uses a very interesting design pattern to allow our `Service` and `Repository` components to talk to each other. We will use a magic **@Autowired** annotation to make this happen. The annotation asks Spring Boot to implement the class for us and then provide a "proxy instance" of the class whenever we ask for it. In other words, Spring Boot will both implement the interface AND instantiate it, and all we have to do is ask for the object where we need it.

Create a class `UserServiceImpl.java` under the same package, implementing the above interface.

```java
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.models.User;
import com.example.springbootmonolith.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

   @Autowired
   private UserRepository userRepository;

}
```

> The **@Service** annotation is a specialization of the `@Component` annotation. Only classes can be marked with this annotation. It is used with classes that have some business logic. Spring will autodetect these classes.

We will now implement the `listUsers()` method and get rid of the annoying compilation error.

```java
@Override
public Iterable<User> listUsers() {
   return userRepository.findAll();
}
```

All we did here was call the `findAll()` method which is provided to us by JPA!

Now all we need to do is add a route in our `UserController` to fetch all users in our database. Just like repository is autowired in service, in the same way, `UserService` will be autowired in the controller.

Here's how `UserController` should look:

```java
package com.example.springbootmonolith.controller;

import com.example.springbootmonolith.models.User;
import com.example.springbootmonolith.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

   @Autowired
   private UserService userService;
   
   @GetMapping("/user/list")
   public Iterable<User> listUsers() {
      return userService.listUsers();
   }

}
```

With this `userService` instance "autowired" into our Controller, we can now start using it to serve up live data! 

Now when we visit [localhost:8080/user/list](http://localhost:8080/user/list), we should see our `User` objects in JSON format. For now you will get an empty array back as there is no user data in the database.

> If we return an Object from a Spring Controller method, that Object will parsed as JSON by default. We can consider this the "V" in our "MVC" pattern.

-----

## Independent Practice: User Signup (20 min)

You now have to write an endpoint `/signup` for user to be able to signup.  Follow all the steps we did earlier, starting from service layer.

Go ahead and add the required method in `UserService` interface first and then implement it in `UserServiceImpl` class.

<details>
<summary>**UserService**</summary>

```java
public interface UserService {
...
   public User createUser(User newUser);
...
}
```

</details>

<details>
<summary>**UserServiceImpl**</summary>

```java
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

We will keep things really simple for now and accept user info (as in username and password) as part of request in body. In Spring we use the **@RequestBody** annotation to do that. You will first write a signup method in `UserController` and then go from there. Our Controller will ask `UserService` nicely to talk to the database and save the above data.

<details>
<summary>**UserController**</summary>

```java
public class UserController {
...
   @PostMapping("/signup")
   public User createUser(@RequestBody User newUser) {
      return userService.createUser(newUser);
   }
...
}
```

</details>

That's it, that is how simple it is. You can now check the `/signup` endpoint to see if it is working. We will use Postman to test this route. From now onwards Postman will be our client. You will send a JSON object with `username` and `password` in the POST request.

```
{
   "username" : "batman",
   "password" : "robin"
}
```

What you will get back in response will be a user onject in JSON format, having the auto generated `id`.

```
{
   "id": 1,
   "username": "batman",
   "password": "robin"
}
```

![](https://git.generalassemb.ly/GA-Cognizant/spring-boot/blob/master/spring-data-postgresql-lesson/images/postman_post_user_request.png)

-----

## Adding More User Functionality (15 min)

### User Login

What if now you want to add an endpoint for logging in the User? In order to do that, you need to check both username and password if they match. There is no custom method in `CrudRepository` for you to select a row on the basis of both username and password. Of course one way is to get the user object from database using method `findById()` and then match the password OR the other way you can do is create a custom method using **@Query** annotation.

#### @Query

Lets just say you need to execute a custom query which is not a part of the CrudRepository. What you do is annotate the method with `@Query` annotation and give JPQL or SQL in its value attribute to execute. The @Query annotation takes precedence over named queries, which are annotated with @NamedQuery or defined in an orm.xml file.

It’s a good approach to place a query definition just above the method inside the repository rather than inside our domain model as named queries. The repository is responsible for persistence, so it’s a better place to store these definitions.

**UserRepository**

```
package com.example.springbootmonolith.repositories;

import com.example.springbootmonolith.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

   @Query("FROM User u WHERE u.username = ?1 and u.password = ?2")
   public User login(String username, String password);

}
```

> The query above uses AND operator to check both username and password if they match

Now go ahead and make the required changes in controller and service to call the newly created method. We will follow the exact steps we have been doing so far.

**UserService.java**

```
public interface UserService {
...
   public User login(String username, String password);
...
}
```

**UserServiceImpl.java**

```
public class UserServiceImpl implements UserService {
...
   @Override
   public User login(String username, String password) {
      return userRepository.login(username, password);
   }
...
}
```

**UserController.java**

```
public class UserController {
...
   @GetMapping("/login/{username}/{password}")
   public User login(@PathVariable String username, @PathVariable String password) {
         return userService.login(username, password);
   }
...
}
```

**@PathVariable** annotation use a simple syntax to pluck request parameters from our URL. First, we must define the variable in the `GetMapping` using the `{variableName}` syntax. As we can see above, our URL defines a new `PathVariable` called `username`. Once we have defined this variable in the URL, we can set up an annotated parameter in the route's method, using the same name. The variables will be assigned in the order that they are declared. 

That's all you need to do and after your server is restarted create a user again by making POST request to `/signup` route. Then, check the [http://localhost:8080/login/batman/robin](http://localhost:8080/login/batman/robin) by making the GET request.

### Deleting a User

We can use a similar combination of `PathVariables` and predefined Repository methods to delete a user. Let's add the `DELETE` route now. We'll return a HTTP status of `200`, to indicate that the user was deleted successfully:

**UserController**

```java
public class UserController {
...
   @DeleteMapping("/user/{userId}")
   public HttpStatus deleteUserById(@PathVariable Long userId) {
      return userService.deleteById(userId);
   }
...
}
```

Now make the required changes in `UserService` and it's impl.

**UserService.java**

```
public interface UserService {
...
   public void deleteById(Long userId);
...
}
```

**UserServiceImpl.java**

```
public class UserServiceImpl implements UserService {
...
   @Override
   public void deleteById(Long userId) {
      userRepository.deleteById(userId);
   }
...
}
```

Test the route [http://localhost:8080/user/1](http://localhost:8080/user/2) to make sure user is getting deleted successfully.

---

## Lab Time

Now, we'll move to [Step 3a](https://git.generalassemb.ly/GA-Cognizant/spring-boot/tree/master/spring-boot-lab#step-3-spring-data) of our music app lab.

----

# Part 2

## Relational Mapping (30 min)

As you already know from previous lessons that Postgres is a relational database that means that almost all the tables are related to each other. You already know about that relationship, we are now going to see their implementation in Spring Data. Like before everything will be done through annotations.

Before we move to actual coding let's set the stage here. We have our User table which currently has 3 columns `id`, `username` and `password`. The next thing we'd like to do is now maintain a User Profile that means we now need a placeholder to collect other information about the User like email, mobile no and address. In an ideal world all these 3 things might be stored in other tables but since we are in a learning phase here we will keep things simple. We will keep all 3 as String.

### Code Along

Take this time to create a `UserProfile` model that will have 4 fields `profileId`, `email`, `mobile` and `address`. The table name will be `user_profile`. Follow the steps we did above when we create `User` model

**UserProfile.java**

```
package com.example.springbootmonolith.models;

import javax.persistence.*;

@Entity
@Table(name = "user_profile")
public class UserProfile {
   
   @Id
   @Column
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column
   private String email;

   @Column
   private String mobile;

   @Column
   private String address;

   public UserProfile() {}
   
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }
   
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

We now have the `UserProfile` but it is still not connected to our User table. What do you think will be the relation between `User` and it's profile? Each User will have only one Profile that means that these tables will have one-to-one relationship.

### @OneToOne

This is the annotation we will use in our `User` class to connect it to `UserProfile`. This is how the code will work,

**User**

```
public class User {
...
   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name="user_profile_id")
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
> Getter and Setter methods are important. Spring Boot uses them behind the scenes to initialize the object.

**@OneToOne** annotation as the name implies is used to setup one-to-one relationship between the two tables. `cascade = CascadeType.ALL` applies all cascading effects to the related entity. That is, whenever we update/delete a `User` entity, update/delete the corresponding `UserProfile` as well. You have a bunch of options to choose from, you can read more about cascade [here](https://howtodoinjava.com/hibernate/hibernate-jpa-cascade-types/).

**@JoinColumn** annotation is used to specify the foreign key column. If you remember from the database lesson the column used to join the tables together is known as foreign key. In the above case `user_profile` table will be joined using the `id` which is its primary key. And the name of that column in the `users` table will be `user_profile_id`.

There is also something known as bi-directional relationship in mapping using JPA/Hibernate. What we can do is add an instance of `User` in `UserProfile` using `@OneToOne` annotation. This way whenever we want to get the `UserProfile` we will also get `User` data embedded in it. Whether to have bi-directional relationship becomes a design decision but it is usually preferred by architects to have in the application for ease of use. 

Let's update our `UserProfile` model.

**UserProfile**

```
public class UserProfile {
...
   @OneToOne(mappedBy = "userProfile", cascade={CascadeType.DETACH,
         CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
   private User user;
   
   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

...
}
```

> `mappedBy` attribute has value `userProfile` which is what is given in `User` class while declaring `UserProfile` in it.

In bi-directional however only one entity is the owner of the relationship. Most often, the child entity is the owner of the relationship and the parent entity is the inverse side of the relationship. The owner of the relationship contains a @JoinColumn annotation to specify the foreign key column, and the inverse-side of the relationship contains a mappedBy attribute to indicate that the relationship is mapped by the other entity. In our case `User` is owner and `UserProfile` is inverse side of relationship.

### Code Along

We will now create two APIs- first, to add a user's profile if username is given. Second, to get user's profile information if username is given. 

We will create two methods, both returning `UserProfile` object.

**UserProfileRepository**

```
package com.example.springbootmonolith.repositories;

import com.example.springbootmonolith.models.UserProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserProfileRepository extends CrudRepository<UserProfile, Long>{

   @Query("from UserProfile up left join User u on u.username = ?1 and up.id = u.userProfile.id")
   public UserProfile findProfileByUsername(String username);
}
```

> The above query is a left join between `UserProfile` and `User` table.

**UserProfileService**

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.models.UserProfile;

public interface UserProfileService {

   public UserProfile createUserProfile(String username, UserProfile newProfile);

   public UserProfile getUserProfile(String username);
}

```

**UserProfileServiceImpl**

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.models.User;
import com.example.springbootmonolith.models.UserProfile;
import com.example.springbootmonolith.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

   @Autowired
   UserProfileRepository userProfileRepository;

   @Autowired
   UserService userService;

   @Override
   public UserProfile createUserProfile(String username, UserProfile newProfile) {
      User user = userService.getUser(username);
      user.setUserProfile(newProfile);
      return userService.createUser(user).getUserProfile();   
   }

   @Override
   public UserProfile getUserProfile(String username) {
      return userProfileRepository.findProfileByUsername(username);
   }
}

```

**UserProfileController**

```
package com.example.springbootmonolith.controller;

import com.example.springbootmonolith.models.UserProfile;
import com.example.springbootmonolith.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

   @Autowired
   UserProfileService userProfileService;

   @GetMapping("/{username}")
   public UserProfile getUserProfile(@PathVariable String username) {
      return userProfileService.getUserProfile(username);
   }

   @PostMapping("/{username}")
   public UserProfile createUserProfile(@PathVariable String username, @RequestBody UserProfile userProfile) {
      return userProfileService.createUserProfile(username, userProfile);
   }
}
```

We'll also need to update ``UserService`` and ``UserRepository``

**UserService**

```
public class UserService {
...
   public User getUser(String username);
...
}
```

**UserServiceImpl**

```
public class UserServiceImpl {
...
   @Override
   public User getUser(String username) {
      return userRepository.findByUsername(username);
   }
...
}
```

**UserRepository**

```
public interface UserRepository extends CrudRepository<User, Long> {
...
   public User findByUsername(String username);
...
}
```

Run and test both GET & POST routes for `/profile` API. Of course you will have to create a user first. Did anyone get any kind of error while making the GET request? 

This is *Jackson JSON infinite recursion problem*. Due to bi-directional relationship, as both User and UserProfile are embedded within each other. Jackson gets into an infinite loop while populating the objects within each other. There are few ways we can resolve this issue. We will find ways to resolve it as we go along. You can also read about it [here](https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion).

For now we will take the simplest approach and use **@JsonIgnore** annotation to tell Jackson to ignore `User` object in `UserProfile` while sending the response back.

**UserProfile**

```
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserProfile {
...
   @JsonIgnore
   @OneToOne(mappedBy = "userProfile", cascade={CascadeType.DETACH,
         CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
   private User user;
...
}
```
We are simply ignoring one side of the relationship to break the chain. Run and test your app again.

### @ManyToOne

Now in our database we have a User table and a UserProfile table and they share a one-to-one relationship. We now want to add roles for a User. We will have different types of roles and each User will have one role assigned to it. In the real world roles are used for user authorization. For instance, a user with admin role will have way more access than another user. Usually a user can have more than one role but for the purpose of our application, we will make a user have only one role. And this brings us to our next type of relationship- one-to-many OR many-to-one. 

Just like we did in one-to-one example above, this will also be bi-directional, that means both `User` and `UserRole` objects will have reference to each other.

### Code Along

Again what you'll do is create a `UserRole` class in `com.example.springbootmonolith.models` package which will have 2 fields `id` & `name`.

**UserRole.java**

```
package com.example.springbootmonolith.models;

import javax.persistence.*;

@Entity
@Table(name = "user_role")
public class UserRole {

   @Id
   @Column
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Column(unique = true)
   private String name;

   public UserRole() {}

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}

```

After this, first thing we will do is update our `User` class to connect to `UserRole`. In this case we will use **ManyToOne** annotation because like mentioned earlier many users can have one role. This will be followed by `@JoinColumn` where we also say that role cannot be null for any user.

**User**

```
public class User {
...

   @ManyToOne(cascade = {CascadeType.DETACH,
         CascadeType.MERGE, CascadeType.REFRESH})
   @JoinColumn(name = "user_role_id", nullable = false)
   private UserRole userRole;
      
   public UserRole getUserRole() { return userRole; }
   
   public void setUserRole(UserRole userRole) { this.userRole = userRole; }
...
}
```

> The cascade option in this case will be everything except for persist and remove. We don't want that if a User is deleted, the corresponding role should also be deleted in the `user_role` table. Also, the roles in role table will already be added and we don't want to add a new row in the `user_role` table everytime a user is created.

As we are making the relationship bi-directional, we will also update `UserRole` class with a list of users.

**UserRole**

```
public class UserRole {
...
   @OneToMany(mappedBy = "userRole",
         cascade = CascadeType.ALL)
   private List<User> users;
   
   public void setUsers(List<User> users){ this.users = users; }

   public List<User> getUsers(){ return users; }

...
}
```

> `CascadeType.ALL` means that along with other things if a user role is deleted all the users that have that role will also be deleted. This again is a design question. But for simplicity sake we have decided to go with it.

Now in terms of APIs for now we only want to give two functions to our users. First, a user should be able to create a new role. Second, a user should be able to get a role based on the role name. Creating a new role is fairly simple, JPA has already given you `save()` method for it. 

What we will discuss is how to find a role by name? How do you think we can do that? The answer is in the question itself. For the search queries that we need to customize, in our case finding role by name. What we can do is create a method like `findByName()` pass in a String as input and viola JPA executes a search query based on the field. The convention is that field name should come after string `findBy`.

Now go ahead and add its respective dao, service and controller classes.

**UserRoleRepository**

```
package com.example.springbootmonolith.repositories;

import com.example.springbootmonolith.models.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {
   public UserRole findByName(String name);
}
```

**UserRoleService**

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.models.UserRole;

public interface UserRoleService {

   public UserRole createRole(UserRole newRole);

   public UserRole getRole(String roleName);
}
```

**UserRoleServiceImpl**

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.models.UserRole;
import com.example.springbootmonolith.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService {

   @Autowired
   UserRoleRepository userRoleRepository;

   @Override
   public UserRole createRole(UserRole newRole) {
      return userRoleRepository.save(newRole);
   }

   @Override
   public UserRole getRole(String roleName) {
      return userRoleRepository.findByName(roleName);
   }
}
```

**UserRoleController**

```
package com.example.springbootmonolith.controller;

import com.example.springbootmonolith.models.UserRole;
import com.example.springbootmonolith.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class UserRoleController {

   @Autowired
   UserRoleService roleService;

   @GetMapping("/{rolename}")
   public UserRole getRole(@PathVariable String rolename) {
      return roleService.getRole(rolename);
   }

   @PostMapping
   public UserRole createRole(@RequestBody UserRole role) {
      return roleService.createRole(role);
   }

}
```

Run your project. To test the APIs, first we will create a new user by sending a POST request to [http://localhost:8080/signup](http://localhost:8080/signup). Did you get a error, something like `"org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation`. Any idea why we are getting this error? 

If you remember while adding `userRole` to `User` class we said that while saving the User, role should not persist into the `user_role` table. In hibernate entity class unless you mark a field with **@Transient** annotation which means that this field should not be persisted into the database, hibernate will look for the corresponding column. In our case, we don;t want to create a row in `user_role` table while creating a new user so we will have to explicitly set role data retrieved from the database into our user object before saving it into the `users` table. We will make a small update in our user service while saving the user.

**UserServiceImpl**

```
public class UserServiceImpl implements UserService {
...
   @Autowired
   UserRoleService userRoleService;
   
   @Override
   public User createUser(User newUser) {
      UserRole userRole = userRoleService.getRole(newUser.getUserRole().getName());
      newUser.setUserRole(userRole);
      return userRepository.save(newUser);
   }
...
}
```

Now, first create a new role by sending POST request to [http://localhost:8080/role](http://localhost:8080/role) with request body,

```
{
   "name" : "ROLE_ADMIN"
}
```

After this create a new user using `/signup` API with request body,

```
{
   "username" : "spiderman",
   "password" : "spider",
   "userRole" : {
      "name" : "ROLE_ADMIN"
   }
}
```

Just like last time you will see *Jackson JSON infinite recursion problem* again. This time we will use another way of resolving it. **@JsonIdentityInfo** annotation helps with the serialization of entities with bidirectional relationship. We will add the class level annotation to our `User` and `UserRole` models.

**User**

```
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

@JsonIdentityInfo(
      generator = ObjectIdGenerators.PropertyGenerator.class,
      property = "id")
@Entity
@Table(name = "users")
public class User {
...
}
```

**UserRole**

```
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
      generator = ObjectIdGenerators.PropertyGenerator.class,
      property = "id")
@Entity
@Table(name = "user_role")
public class UserRole {
...
}
```

### @ManyToMany

Our client now says that each user can enroll in many courses. So for our many to many example we will create an entirely new model `Course`. Each user can enroll in multiple courses and of course each course can have multiple users.

-----

## You Do (20 min)

Follow earlier steps to create a `Course` model with 3 fields `id`, `code` and `name`. Add two APIs, first to save a course `/course` and second, to list all courses `/course/list`.

<details><summary>**Course**</summary>

```
package com.example.springbootmonolith.models;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "course")
public class Course {
   @Id
   @Column
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Column
   private String code;

   @Column
   private String name;
   
   public Course() { }
   
   public int getId() {
      return id;
   }

   public void setId(int id) {
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
</details>

<details><summary>**CourseRepository**</summary>

```
package com.example.springbootmonolith.repositories;

import com.example.springbootmonolith.models.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Integer> {
}

```
</details>

<details><summary>**CourseService**</summary>

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.models.Course;

public interface CourseService {

   public Course createCourse(Course course);

   public Iterable<Course> listCourses();
}
```
</details>

<details><summary>**CourseServiceImpl**</summary>

```
package com.example.springbootmonolith.service;

import com.example.springbootmonolith.models.Course;
import com.example.springbootmonolith.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService {

   @Autowired
   CourseRepository courseRepository;

   @Override
   public Course createCourse(Course course) {
      return courseRepository.save(course);
   }

   @Override
   public Iterable<Course> listCourses(){
      return courseRepository.findAll();
   }
}
```
</details>

<details><summary>**CourseController**</summary>

```
package com.example.springbootmonolith.controller;

import com.example.springbootmonolith.models.Course;
import com.example.springbootmonolith.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController {

   @Autowired
   CourseService courseService;

   @PostMapping
   public Course createCourse(@RequestBody Course course){
      return courseService.createCourse(course);
   }

   @GetMapping("/list")
   public Iterable<Course> listCourses(){
      return courseService.listCourses();
   }

}

```
</details>

Restart your server. Use Postman to make the POST request [http://localhost:8080/course](http://localhost:8080/course)

```
{
   "code" : "SHI",
   "name" : "Super Hero Immersive"
}
```

Also, don't forget to test GET request to get all courses [http://localhost:8080/course/list](http://localhost:8080/course/list).

------

## Join Table (15 min)

If you remember from SQL lessons in relational database many-to-many mapping is achieved by using a join table. A join table is a special table that provides mapping between any two tables. It has foreign keys for each table to define the mapping relationship. If this definition seems confusing don't worry, things will be much clearer with an example.

In our case we have a `user` table and a `course` table and we say many users can enroll in multiple courses. For that we will create a join table named `user_course` which will have two columns, `user_id` and `course_id` to maintain the relationship between users and courses. What Hibernate does for us, generous that it is, that it will create this table for us just by using `@JoinTable` annotation. But first we'll start by updatting our `Course` class to have a reference of users.

**Course**

```
public class Course {
...
   @ManyToMany(fetch = FetchType.LAZY,
         cascade = {CascadeType.DETACH,
               CascadeType.MERGE, CascadeType.REFRESH})
   @JoinTable(name = "user_course",
         joinColumns = {@JoinColumn(name = "course_id")},
         inverseJoinColumns = @JoinColumn(name = "user_id"))
   private List<User> users;
   
   public List<User> getUsers(){ return users; }

   public void setUsers(List<User> users) { this.users = users; }
...
}
```

**ManyToMany** as the name suggests is used to declare many-to-many mapping. Immediately after that we use **@JoinTable** annotation. It basically tells hibernate which join table we'll use. Along with that we also have to specify `joinColumns`, which will refer to the `course_id` column in the `user_course` join table. And then for other side `inverseJoinColumns` hibernate will look at `user_id` column in `user_course` join table. As we are defining the relationship in `Course` the `User` class is on the other side hence it is considered as inverse. 

Simillarly in `User` it will be just the opposite. `course_id` will be the inverse join column.

**User**

```
public class User {
...
   @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH,
                  CascadeType.MERGE, CascadeType.REFRESH})
   @JoinTable(name = "user_course",
         joinColumns = {@JoinColumn(name = "user_id")},
         inverseJoinColumns = @JoinColumn(name = "course_id"))
   private List<Course> courses;
   
   public List<Course> getCourses(){ return courses; }

   public void setCourses(List<Course> courses) { this.courses = courses; }

...
}
``` 

Till now we can create a new course. We also have the mapping between `User` and `Course`. The next step will be to add an API to let user enroll in a course. The first thing we will do is add a utility method in `User` class to add a course.

**User**

```
public class User {
...
   public List<Course> addCourse(Course course){
      if(courses == null)
         courses = new ArrayList<>();
      courses.add(course);

      return courses;
   }
...
}
``` 

Then we'll update our `UserService` with a new method to add course to a user.

**UserService**

```
public interface UserService {
...
   public User addCourse(String username, int courseId);
...
}
```

**UserServiceImpl**

```
public class UserServiceImpl implements UserService {
...
   @Autowired
   CourseRepository courseRepository;
   
   @Override
   public User addCourse(String username, int courseId) {
      Course course = courseRepository.findById(courseId).get();
      User user = getUser(username);
      user.addCourse(course);

      return userRepository.save(user);
   }
...
}
```

**UserController**

```
public class UserController {
...
   @PutMapping("/user/{username}/{courseId}")
   public User addCourse(@PathVariable String username, @PathVariable int courseId){
      return userService.addCourse(username, courseId);
   }
...
}
```

Restart the server and create a new course. Now test the PUT route [http://localhost:8080/user/spiderman/1](http://localhost:8080/user/spiderman/1).

----

## Independent Practice (20 min)

Pair up, take couple of minutes to draw an ERD of the database that we have built today. The ERD should clearly show all the fields in tables and the relationship between them

Also, test all the APIs that we created today to make sure everything is working as expected.

POST [http://localhost:8080/course](http://localhost:8080/course)

GET [http://localhost:8080/course/list](http://localhost:8080/course/list)

POST [http://localhost:8080/role](http://localhost:8080/role)

GET [http://localhost:8080/role/{rolename}](http://localhost:8080/role/ROLE_ADMIN)

POST [http://localhost:8080/signup](http://localhost:8080/signup)

GET [http://localhost:8080/login/{username}/{password}](http://localhost:8080/login/batman/robin)

POST [http://localhost:8080/profile/{username}](http://localhost:8080/profile/batman)

GET [http://localhost:8080/profile/{username}](http://localhost:8080/profile/batman)

GET [http://localhost:8080/user/list](http://localhost:8080/user/list)

PUT [http://localhost:8080/user/{username}/{courseId}](http://localhost:8080/user/batman/robin)

DELETE [http://localhost:8080/user/{userId}](http://localhost:8080/user/1)

> While deleting the user make sure to check the database to verify the cascade property that we had set for each table mapping with `User` is working as expected.

----

## Conclusion (5 min)

We have finally come to the end of this lesson. This has been one long lesson with lots on new concepts covered.

![](https://media0.giphy.com/media/Nku1xy9Kdo0Tu/source.gif)

Do not worry if this all seems a bit too much. With enough practice things will start to come together pretty soon.

----

## Lab Time

Now we can move into the [Step 3B](https://git.generalassemb.ly/GA-Cognizant/spring-boot/tree/master/spring-boot-lab#3b) of the Spring Boot music player app.

#### Additional Reading

You can read more about auto generating the id [here](https://www.objectdb.com/java/jpa/entity/generated#The_Auto_Strategy_).

