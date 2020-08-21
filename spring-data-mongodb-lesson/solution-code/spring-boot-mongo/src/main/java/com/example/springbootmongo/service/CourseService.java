package com.example.springbootmongo.service;

import com.example.springbootmongo.model.Course;

import java.util.List;

public interface CourseService {

    public Course saveCourse(Course course);

    public List<Course> listCourses();
}