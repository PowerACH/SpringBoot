| Title | Type | Duration | Author |
| -- | -- | -- | -- |
| Spring Profiles | Lesson | 0:30 | IA |

# Spring Profiles

## Learning Objectives

*After this lesson, students will be able to:*

- Use Spring Profiles to configure a Spring Boot application.

## LESSON GUIDE

| TIMING  | TYPE  | TOPIC  |
|:-:|---|---|
| 5 min  | Opening    | Lesson Objectives |
| 20 min | Guided Practice   | Setting Up Spring Profiles  |
| 5 min  | Conclusion | Review/Recap   |

## Introduction (5 min)

[Spring Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html) provides a way to segregate parts of your application configuration and limit its availability to certain environments. Applications are developed, tested, and run on different environments. 

Different configurations are needed for different environments. Maybe your database credentials change from dev to staging, or your application is deployed on a different port number on staging and prod. Spring Profiles gives you an easy way to handle these changes as you move from one environment to another. 

For instance, you can use the **@Profile** annotation with any `@Component` or `@Configuration` to specify the environment in which it is loaded:

```
@Configuration
@Profile("dev")
```

-----

## Setting Up Spring Profiles (20 min)

There are many properties that can be added to configure a Spring Boot app. We're not going to cover each of them individually. Instead, we'll cover a few of them and how they affect the environment.   

### Getting Started
  
For this exercise, use the application built previously in the [Spring Boot Overview](https://git.generalassemb.ly/GA-Cognizant/spring-boot/tree/master/spring-boot-overview-lesson) lesson. Or, you can download the app provided in this lesson's [starter code](https://git.generalassemb.ly/GA-Cognizant/spring-boot/tree/master/spring-profiles-lesson/starter-code/spring-boot-monolith).

Create a new file, `application.properties`, under the `/resources` folder. Add the following properties to it. Because we are developing our app right now, we'll assume that we're working in a dev environment:

```
spring.profiles.active=dev
server.port=8181
logging.level.org.springframework.web=DEBUG
```

**What's Going On?**

- The `server.port` property allows us to specify a different port from the default (port 8080). For illustrative purposes, we'll use port 8181. 
- The `logging.level.org.springframework.web` property allows us to adjust the logging level in our application. This is especially helpful when we're debugging an issue and need more details compared to when the logging level is set to `INFO`. There's no need for a framework such as Log4j.   

If you restart your application, you'll notice additional logging information available in the IDE console. Additionally, your application should now be deployed on port 8181. 

Create another file called `application-dev.properties`. This file will be used for all further configurations specific to the dev environment.

### Bonus!

You can also do this programmatically in Spring Boot by setting active profiles, calling `SpringApplication.setAdditionalProfiles(…​)` before the application runs. 

It's also possible to activate profiles using Spring’s `ConfigurableEnvironment` interface:

```
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DemoApplication.class);
	
		application.setAdditionalProfiles("dev");
		
//		ConfigurableEnvironment environment = new StandardEnvironment();
//		environment.setActiveProfiles("qa");
//		application.setEnvironment(environment);
		
		application.run(args);
	}
}
```

---

## Conclusion (5 min)

We'll continue to add properties to this file in our application as we go (and as our apps get more complex).

# Lab: Spring Music App

You'll now work on building a back-end app incrementally. This lab has been divided into a series of steps that you'll work on as we progress. You can access the lab [here](https://git.generalassemb.ly/nyc-s1-1/spring-boot/tree/master/spring-boot-lab).

For today, work on [Step 1](https://git.generalassemb.ly/nyc-s1-1/spring-boot/tree/master/spring-boot-lab#step-1-spring-boot) and [Step 2](https://git.generalassemb.ly/nyc-s1-1/spring-boot/tree/master/spring-boot-lab#step-2-spring-profile) of the given exercise.`
