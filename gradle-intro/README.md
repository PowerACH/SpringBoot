---
title: Intro to Gradle
type: Morning Exercise
duration: "1:00"
creator:
    name: Melissa Arliss
---

# ![](https://ga-dash.s3.amazonaws.com/production/assets/logo-9f88ae6c9c3871690e33280fcf557f33.png) Intro to Gradle

When we first set up our development environment, one of the most important things we installed was [Maven](https://maven.apache.org/), the build tool that we've been using in all of our apps.

> **Knowledge Check**: Who can explain what a "build tool" (or "dependency management tool") does for our applications?

As with almost anything in web development, Maven is not the only tool for the job. In fact, there are a host of build tools that can be leveraged depending on the language and framework you're using, the size and complexity of your application, and the types of dependencies with which you're working.

Maven is a popular tool for Java applications, but the new kid on the block, [Gradle](https://gradle.org/), is quickly catching up in popularity.

### What's Gradle?

*"Gradle is an open-source build automation tool that is designed to be flexible enough to build almost any type of software."*

Let's compare that to how Maven is described:

*"Maven’s primary goal is to allow a developer to comprehend the complete state of a development effort in the shortest period of time."*

> **Knowledge Check**: What jumps out at you as key differences between these two descriptions?

There are a lot more differences between these tools than just their taglines. So let's learn more!

With a partner, check out the [Gradle docs](https://docs.gradle.org/current/userguide/userguide.html) and answer the following questions:

- What are some key differentiators for Gradle?
- Maven centers on the `pom.xml` file. What is the equivalent central feature for Gradle?
- How does Gradle handle dependency management?

We'll discuss our findings after a few minutes of research.

### Getting Our Hands Dirty

All talk and no practice... that's not how we learn things!

Luckily for us, Gradle provides some free, easy-to-follow tutorials so you can see how it works.

First things first: installation. Follow [this link](https://docs.gradle.org/current/userguide/installation.html) to install Gradle on your system.

Once everything is installed, you'll create your first Gradle build! On your own, take the next 10–15 minutes to go through [this tutorial](https://guides.gradle.org/creating-new-gradle-builds/) on creating new Gradle builds.

We'll discuss once you're done. What did you notice about the Gradle process?

### Gradle in Action

This was a pretty simple Gradle implementation, but as you can imagine, it gets a lot more complicated from here. Let's take a look at an application that uses Gradle [here](./gradle-example-app).

This is a simple application designed to function as a Spotify clone. Take a look at the folder structure and code.

- What jumps out at you?
- What's different compared to how we've been building our apps?

### The Great Dependency Debate

Now that you're a Gradle expert (sort of) and you've been using Maven all the time (like a pro), let's think and discuss: Why would we use one tool over the other?


