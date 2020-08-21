| Title | Type | Duration | Author |
| -- | -- | -- | -- |
| Spring Boot Lab | Lab | 15:00 | Isha Arora |  

# ![](https://ga-dash.s3.amazonaws.com/production/assets/logo-9f88ae6c9c3871690e33280fcf557f33.png) Create a Music App Lab

## Opening 

We're going to create a [Spotify](http://www.spotify.com) clone during this Spring week. You'll continue to add functionality to your app as we learn more about Spring and its modules. 

This will be a fairly open-ended lab. We want you to be able to think through the design decisions and understand the reason why you choose one thing over the other. There's just one main ground rule to follow: Make sure you only work with the technologies/tools we covered during the lesson.

You will **work in pairs** for all of the different steps. Try to stick with the same partner for the entirety of this lab. 

------

## Deliverables

Collaborate and push your changes to a GitHub repo.

You will maintain a `README` that will include the following:

- A link to the GitHub repo.
- All of your design decisions. 
- The reasons behind each decision.
- What went right.
- Challenges you faced.
- Which part you enjoyed working on the most.

At the end of this lab, after testing is done, we will hear a **five-minute presentation** from each team. Each team will go through its `README`, talking through each point. Be prepared to answer some questions. 

----

## Exercise

We'll build this back-end app incrementally.

### Step 1 (Spring Boot)

Today, you'll just set up your app using Spring Boot CLI, as we did in the lesson earlier. 

- Use Maven to download and build all of the dependencies.
- Create a REST controller.
- Create a `/hello` endpoint that returns a `'Hello World!'` string.
- Use Postman to test the API.

### Step 2 (Spring Profile)

In the next step, you'll use Spring Profile to create a development-specific environment in your app. This is where all of your environment-specific configuration will go.

### Step 3 (Spring Data)

Your Spotify app will have at least two data models: `User` and `Song`. You can choose to add more models if you think it is required. This decision will be totally up to you. You'll use PostgreSQL as your database and Spring Data to talk to your database.

This step will be done in two parts. 

#### 3(a)

In this step, you'll only create a `User` model. 

- The `User` should have at least two fields: `username` and `password`.
- You'll create APIs for user signup and login.
- If there is time left, you'll also create APIs to update and delete a user.

#### 3(b)

In the next step, you'll add the `Song` model and map it with `User`.

- The `Song` table should have at least two columns: `title` and `length`.
- The `User` and `Song` tables should be mapped to each other.
- You should only add a song to any user if the user is present. <!-- this is confusing; "present" means authenticated or not-null? I assume the latter. -->
- A user can add and delete a song from a profile.
- A user should be able to view all of the songs added by the user.

Again, the mapping between the `User` and `Song` models is your decision. You can either go for one-to-many or many-to-many mapping. Please know that you should be able to defend all of your design decisions.

It is strongly recommended that you add a `UserRole` object in your app. This will make your work easier in the next step of this lab (when we cover Spring Security). Don't worry if you're not able to work on it now; you'll get plenty of time later to add it. 

- `UserRole` should have at least one field `name` of the role.
- Each user should have at least one role attached to it.
- A user should be able to create and get a role.

Again, you can choose whichever way you want to map it with the user. As mentioned before, you should know the reason for going with one option over the other.

If time permits, make sure to add more models in your app; just make sure it's all relational. You can create as many APIs as you want to.

### Step 4 (Spring Security)

You'll now add user authentication and authorization to your existing Spotify app, using JSON Web Tokens (JWT) to authenticate your requests. You already have a `User` model for security purposes.

- Again, make sure to use only JWT for authentication.
- Both login and sign-up APIs will only return a JWT in response.
- All other requests will use that token for authentication.

In case you couldn't add `UserRole` earlier, make sure to add it now before moving on to adding authorization. 

- Only a user with admin access can create and view roles.
- Only admins can delete a user.
- All other requests can be made by any user.
- Bonus feature: Only allow a user to add songs for themselves. Only admins should be allowed to add for any user. You'll need to determine who the logged-in user is in order to do so.

### Step 5 (Spring Testing)

You have completed app development and now want to unit test your app. You'll have to use both mocking and stubbing for that. How you choose to test every part of your code is up to you. Remember, both these methods are equally important and useful. 

The main objective here: First, get maximum practice in both mocking and stubbing. Second, test as much code as possible in the time given. 
